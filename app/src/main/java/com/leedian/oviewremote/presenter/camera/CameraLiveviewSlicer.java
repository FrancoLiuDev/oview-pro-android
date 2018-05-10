/*
 * Copyright 2014 Sony Corporation
 */

package com.leedian.oviewremote.presenter.camera;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

/**
 * A parser class for Live view data Packet defined by Camera Remote API
 */
class CameraLiveViewSlicer {

    private static final String TAG = CameraLiveViewSlicer.class.getSimpleName();
    private static final int CONNECTION_TIMEOUT = 2000;
    private HttpURLConnection mHttpConn;
    private InputStream mInputStream;

    /**
     * Converts byte array to int.
     *
     * @param byteData  convert byte
     * @param startIndex index
     * @param count    cnt
     * @return int
     */
    private static int bytesToInt(byte[] byteData, int startIndex, int count) {
        int ret = 0;
        for (int i = startIndex; i < startIndex + count; i++) {
            ret = (ret << 8) | (byteData[i] & 0xff);
        }
        return ret;
    }

    /**
     * Reads byte array from the indicated input stream.
     *
     * @param in    InputStream
     * @param length length
     * @return byte[]
     * @throws IOException
     */
    private static byte[] readBytes(InputStream in, int length) throws IOException {
        ByteArrayOutputStream tmpByteArray = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            int trialReadLen = Math.min(buffer.length, length - tmpByteArray.size());
            int readCnt = in.read(buffer, 0, trialReadLen);
            if (readCnt < 0) {
                break;
            }
            tmpByteArray.write(buffer, 0, readCnt);
            if (length <= tmpByteArray.size()) {
                break;
            }
        }
        byte[] ret = tmpByteArray.toByteArray();
        tmpByteArray.close();
        return ret;
    }

    /**
     * Opens Live view HTTP GET connection and prepares for reading Packet data.
     *
     * @param liveViewUrl Live view data url that is obtained by DD.xml or result
     *                    of start Live view API.
     * @throws IOException generic errors or exception.
     */
    public void open(String liveViewUrl) throws IOException {
        if (mInputStream != null || mHttpConn != null) {
            throw new IllegalStateException("Slicer is already open.");
        }

        final URL urlObj = new URL(liveViewUrl);
        mHttpConn = (HttpURLConnection) urlObj.openConnection();
        mHttpConn.setRequestMethod("GET");
        mHttpConn.setConnectTimeout(CONNECTION_TIMEOUT);
        mHttpConn.connect();

        if (mHttpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            mInputStream = mHttpConn.getInputStream();
        }
    }

    /**
     * Closes the connection.
     */
    public void close() {
        try {
            if (mInputStream != null) {
                mInputStream.close();
                mInputStream = null;
            }
        } catch (IOException e) {
            Log.w(TAG, "Close() IOException.");
        }

        if (mHttpConn != null) {
            mHttpConn.disconnect();
            mHttpConn = null;
        }
    }

    Payload nextPayload() throws IOException {

        Payload payload = null;

        while (mInputStream != null && payload == null) {
            // Common Header
            int readLength = 1 + 1 + 2 + 4;
            byte[] commonHeader = readBytes(mInputStream, readLength);
            if (commonHeader == null || commonHeader.length != readLength) {
                throw new IOException("Cannot read stream for common header.");
            }

            if (commonHeader[0] != (byte) 0xFF) {
                throw new IOException("Unexpected data format. (Start byte)");
            }

            switch (commonHeader[1]) {
                case (byte) 0x12:
                    // This is information header for streaming.
                    // skip this packet.
                    readLength = 4 + 3 + 1 + 2 + 118 + 4 + 4 + 24;
                    //commonHeader = null;
                    readBytes(mInputStream, readLength);
                    break;
                case (byte) 0x01:
                case (byte) 0x11:
                    payload = readPayload();
                    break;
                default:
                    break;
            }
        }
        return payload;
    }

    /**
     * Reads live view stream and slice one Packet. If server is not ready for
     * live view data, this API calling will be blocked until server returns next
     * data.
     *
     * @return Payload data of sliced Packet
     * @throws IOException generic errors or exception.
     */
    private Payload readPayload() throws IOException {

        if (mInputStream != null) {
            // Payload Header
            int readLength = 4 + 3 + 1 + 4 + 1 + 115;
            byte[] payloadHeader = readBytes(mInputStream, readLength);
            if (payloadHeader == null || payloadHeader.length != readLength) {
                throw new IOException("Cannot read stream for payload header.");
            }
            if (payloadHeader[0] != (byte) 0x24 || payloadHeader[1] != (byte) 0x35
                    || payloadHeader[2] != (byte) 0x68
                    || payloadHeader[3] != (byte) 0x79) {
                throw new IOException("Unexpected data format. (Start code)");
            }
            int jpegSize = bytesToInt(payloadHeader, 4, 3);
            int paddingSize = bytesToInt(payloadHeader, 7, 1);

            // Payload Data
            byte[] jpegData = readBytes(mInputStream, jpegSize);
            byte[] paddingData = readBytes(mInputStream, paddingSize);

            return new Payload(jpegData, paddingData);
        }
        return null;
    }

    /**
     * Payload data class. See also Camera Remote API specification document to
     * know the data structure.
     */
    static final class Payload {
        /**
         * jpeg data container
         */
        final byte[] jpegData;

        /**
         * padding data container
         */
        final byte[] paddingData;

        /**
         * Constructor
         */
        private Payload(byte[] jpeg, byte[] padding) {
            this.jpegData = jpeg;
            this.paddingData = padding;
        }
    }
}
