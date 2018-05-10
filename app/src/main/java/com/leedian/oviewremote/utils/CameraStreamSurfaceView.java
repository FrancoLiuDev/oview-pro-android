/*
 * Copyright 2014 Sony Corporation
 */

package com.leedian.oviewremote.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * A SurfaceView based class to draw liveview frames serially.
 */
public class CameraStreamSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = CameraStreamSurfaceView.class.getSimpleName();
    private final BlockingQueue<byte[]> mJpegQueue = new ArrayBlockingQueue<byte[]>(2);
    private final Paint mFramePaint;
    private boolean mWhileFetching;
    private Thread mDrawerThread;
    private int mPreviousWidth = 0;
    private int mPreviousHeight = 0;
    private Rect mFrameArea = new Rect();

    private StreamErrorListener mErrorListener;
    private CameraPreviewTouchListener previewTouchListener;

    /**
     * Constructor
     *
     * @param context
     */
    public CameraStreamSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
        mFramePaint = new Paint();
        mFramePaint.setDither(true);
    }

    /**
     * Constructor
     *
     * @param context
     * @param attrs
     */
    public CameraStreamSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        mFramePaint = new Paint();
        mFramePaint.setDither(true);
    }

    /**
     * Constructor
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public CameraStreamSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getHolder().addCallback(this);
        mFramePaint = new Paint();
        mFramePaint.setDither(true);
    }

    public void setPreviewTouchListener(CameraPreviewTouchListener previewTouchListener) {
        this.previewTouchListener = previewTouchListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        PointF touchPos = getFrameTouchPosition(event.getX(), event.getY());

        if (isFrameInTouchPosition(touchPos)) {

            if (previewTouchListener != null)
                previewTouchListener.onTouchPosition(touchPos.x / mFrameArea.width() ,touchPos.y / mFrameArea.height() );
        }

        return super.onTouchEvent(event);
    }

    private PointF getFrameTouchPosition(float x, float y) {

        PointF touchPos = new PointF(x - mFrameArea.left, y - mFrameArea.top);

        return touchPos;
    }

    private boolean isFrameInTouchPosition(PointF pos) {

        if (pos.x < 0 || pos.x > mFrameArea.width()) {
            return false;
        }

        if (pos.y < 0 || pos.y > mFrameArea.height()) {
            return false;
        }

        return true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // do nothing.
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // do nothing.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mWhileFetching = false;
    }

    /**
     * Request to stop retrieving and drawing liveview data.
     */
    public void stop() {
        mWhileFetching = false;
    }

    /**
     * Check to see whether start() is already called.
     *
     * @return true if start() is already called, false otherwise.
     */
    public boolean isStarted() {
        return mWhileFetching;
    }

    /**
     * Draw frame bitmap onto a canvas.
     *
     * @param frame
     */
    public void drawFrame(Bitmap frame) {

        if (frame.isRecycled())
            return;

        if (frame.getWidth() != mPreviousWidth || frame.getHeight() != mPreviousHeight) {
            onDetectedFrameSizeChanged(frame.getWidth(), frame.getHeight());
            return;
        }
        Canvas canvas = getHolder().lockCanvas();
        if (canvas == null) {
            return;
        }
        int w = frame.getWidth();
        int h = frame.getHeight();
        Rect src = new Rect(0, 0, w, h);

        float by = Math.min((float) getWidth() / w, (float) getHeight() / h);
        int offsetX = (getWidth() - (int) (w * by)) / 2;
        int offsetY = (getHeight() - (int) (h * by)) / 2;

        mFrameArea.set(offsetX, offsetY, getWidth() - offsetX, getHeight() - offsetY);
        //Rect dst = new Rect(offsetX, offsetY, getWidth() - offsetX, getHeight() - offsetY);

        if (frame.isRecycled())
            return;

        canvas.drawBitmap(frame, src, mFrameArea, mFramePaint);
        getHolder().unlockCanvasAndPost(canvas);
    }

    /**
     * Called when the width or height of liveview frame image is changed.
     *
     * @param width
     * @param height
     */
    private void onDetectedFrameSizeChanged(int width, int height) {
        Log.d(TAG, "Change of aspect ratio detected");
        mPreviousWidth = width;
        mPreviousHeight = height;
        drawBlackFrame();
        drawBlackFrame();
        drawBlackFrame(); // delete triple buffers
    }

    /**
     * Draw black screen.
     */
    private void drawBlackFrame() {
        Canvas canvas = getHolder().lockCanvas();
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), paint);
        getHolder().unlockCanvasAndPost(canvas);
    }

    public interface CameraPreviewTouchListener {

        void onTouchPosition(float xPosPercent, float yPosPercent);
    }

    public interface StreamErrorListener {

        void onError(StreamErrorReason reason);

        enum StreamErrorReason {
            IO_EXCEPTION,
            OPEN_ERROR,
        }
    }
}
