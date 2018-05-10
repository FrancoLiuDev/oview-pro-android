package com.leedian.oviewremote.presenter.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.leedian.oviewremote.OviewCameraApp;
import com.leedian.oviewremote.utils.BlueToothUtils;
import com.leedian.oviewremote.utils.SystemUtils;
import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by francoliu on 2017/3/21.
 */

public class OviewDeviceManager {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;
    boolean bReadyRotate = true;
    boolean bTerminate = false;
    Context mContext;
    FuctionName fn;
    // DeviceServiceEvent event;
    ArrayList<DeviceServiceEvent> eventlisteners = new ArrayList<>();
    boolean isBound = false;
    private boolean mScanning;
    private boolean mDeviceFound;
    private String mDeviceAddress;
    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private DeviceConnectionLister connectionLister;
    private int[] RGBFrame = {0, 0, 0};
    private BluetoothLeService mBluetoothLeService;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {

            }

            mContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            mBluetoothLeService.disconnect();
            mContext.unregisterReceiver(mGattUpdateReceiver);
            mBluetoothLeService = null;
        }
    };
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;
    private boolean mConnected = false;
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                onEventConnect();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                onEventDisconnect();
                bTerminate = true;
                // mContext.unbindService(mServiceConnection);

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

                displayGattServices(mBluetoothLeService.getSupportedGattServices());

                final Handler mUiHandler = new Handler();
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        connect();
                    }
                });
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String strs = intent.getStringExtra(mBluetoothLeService.EXTRA_DATA);
                strs = null;
                bReadyRotate = true;
            }
        }
    };
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

                    if (mDeviceFound)
                        return;

                    String deviceName = device.getName();
                    String deviceAddress = device.getAddress();

                    if (deviceName == null) return;

                    if (deviceName.equals("OVIEW_LD")) {
                        mDeviceFound = true;
                        mDeviceAddress = deviceAddress;
                        connectionLister.onDeviceConnected(deviceName, mDeviceAddress);
                    }
                }
            };

    public OviewDeviceManager(Context context) {

        mContext = context;
        mHandler = new Handler();
        initBlueToothDevice();
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public boolean isConnected() {
        return mConnected;
    }

    public void setConnected(boolean mConnected) {
        this.mConnected = mConnected;
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;

        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();

        for (BluetoothGattService gattService : gattServices) {

            characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
            characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
        }
    }

    private void connect() {

        if (mBluetoothLeService != null) {

            clearRGB_btn();
        }
    }

    private String getRGBSTR(String deviceName) {

        return fn.LE.toString() + "@" + deviceName + "," + String.valueOf(RGBFrame[0]) + "," + String.valueOf(RGBFrame[1]) + "," + String.valueOf(RGBFrame[2]) + "*";
    }

   /* public void startRotate() {

        RGBFrame[0] = 0;
        RGBFrame[1] = 0;
        RGBFrame[2] = 0;
        makeChange(getRotareString());

    }*/

    private String getRotareString(float degree) {

        int steps = 1;
        // int deviceDegree = (int)(degree / 0.75) ;
        int delay = 10;
        int direction = 0;

        String str = fn.MO.toString() + "@" + String.valueOf(steps) + "," + String.valueOf(degree) + "," + String.valueOf(delay) + "," + String.valueOf(direction) + "*";
        return str;
    }

    private void makeChange(String str) {

        Log.d("mylog", "Sending result=" + str);
        final byte[] tx = str.getBytes();
        if (mConnected) {
            characteristicTX.setValue(tx);
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);

            SystemUtils.acquireDelay(200);
        }
    }

    public void startRotate(float degree) {

        bReadyRotate = false;
        makeChange(getRotareString(degree));
    }

    public void initRotate() {

        bReadyRotate = true;
        bTerminate = false;
    }

    public void setTerminate() {

        bTerminate = true;
    }

    public boolean checkRotateReady() {

        while (!bReadyRotate) {

            try {
                Thread.sleep(200);

                if (bTerminate)
                    break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return bReadyRotate;
    }

    public void clearRGB_btn() {

        RGBFrame[0] = 0;
        RGBFrame[1] = 0;
        RGBFrame[2] = 0;
        makeChange(getRGBSTR("A"));
    }

    public void setColorRGB(LightDirection direction, int r, int g, int b) {

        String str = getDirectionSymbel(direction);
        RGBFrame[0] = r;
        RGBFrame[1] = g;
        RGBFrame[2] = b;
        makeChange(getRGBSTR(str));
    }

    public String getDirectionSymbel(LightDirection direction) {

        if (direction == LightDirection.LEFT_TOP) {
            return "A";
        }

        if (direction == LightDirection.LEFT_MIDDLE) {
            return "B";
        }

        if (direction == LightDirection.LEFT_BOTTOM) {
            return "C";
        }

        if (direction == LightDirection.RIGHT_TOP) {
            return "D";
        }

        if (direction == LightDirection.RIGHT_MIDDLE) {
            return "E";
        }

        if (direction == LightDirection.RIGHT_BOTTOM) {
            return "F";
        }

        if (direction == LightDirection.BACK_LEFT) {
            return "G";
        }

        if (direction == LightDirection.BACK_RIGHT) {
            return "H";
        }

        if (direction == LightDirection.BACKGROUND_TOP) {
            return "I";
        }

        if (direction == LightDirection.BACKGROUND_BACK) {
            return "J";
        }
        if (direction == LightDirection.BACKGROUND_BOTTOM) {
            return "K";
        }
        if (direction == LightDirection.ALL) {
            return "A";
        }
        return "X";
    }

    public void startSearchDevice(DeviceConnectionLister connectionLister) {

        this.connectionLister = connectionLister;

        scanLeDevice(true);
    }

    public void registerEventListner(DeviceServiceEvent event) {

        eventlisteners.add(event);
    }

    public void unregisterEventListner(DeviceServiceEvent event) {

        eventlisteners.remove(event);
    }

    public void onEventConnect() {

        for (int i = 0; i < eventlisteners.size(); i++) {
            DeviceServiceEvent listener = eventlisteners.get(i);
            listener.connect();
        }
    }

    public void onEventDisconnect() {

        for (int i = 0; i < eventlisteners.size(); i++) {
            DeviceServiceEvent listener = eventlisteners.get(i);
            listener.disconnect();
        }
    }

    public void connectToOviewDevice() {

        Intent gattServiceIntent = new Intent(mContext, BluetoothLeService.class);
        isBound = mContext.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    public void disconnectToOviewDevice() {

        if (isBound) {
            mContext.unbindService(mServiceConnection);
            isBound=false;
            setConnected(false);
        }
    }

    public void stopSearchDevice() {

        scanLeDevice(false);
    }

    public void initBlueToothDevice() {

        if (!BlueToothUtils.isSupportBlueTooth(OviewCameraApp.getAppContext())) {
            mBluetoothAdapter = null;
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) OviewCameraApp.getAppContext().getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {

        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

                    if (!mDeviceFound) {
                        connectionLister.onSearchConnectedTimeout();
                    }
                }
            }, SCAN_PERIOD);

            mDeviceFound = false;
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    enum FuctionName {
        SE,
        LE,
        MO,
        SY
    }

    public interface DeviceServiceEvent {
        void disconnect();

        void connect();
    }

    public interface DeviceConnectionLister {

        void onDeviceConnected(String name, String addr);

        void onSearchConnectedTimeout();
    }
}
