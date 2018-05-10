package com.leedian.oviewremote.view.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import butterknife.Bind;

import com.leedian.oviewremote.AppManager;
import com.leedian.oviewremote.AppResource;
import com.leedian.oviewremote.R;
import com.leedian.oviewremote.base.baseView.BaseActivity;
import com.leedian.oviewremote.navigator.AppNavigator;
import com.leedian.oviewremote.presenter.presenterImp.MainStatePresenterImp;
import com.leedian.oviewremote.presenter.presenterInterface.MainStatePresenter;
import com.leedian.oviewremote.utils.viewUtils.ActionBarTop;
import com.leedian.oviewremote.view.states.AppMainViewState;
import com.leedian.oviewremote.view.viewInterface.ViewMainState;

public class MainStateActivity extends BaseActivity<ViewMainState, MainStatePresenter>
        implements ViewMainState, View.OnClickListener, ActionBarTop.ActionButtonEvent {

    protected ActionBarTop actionBar;
    AppNavigator navigator = new AppNavigator(this);
    @Bind(R.id.image_camera_state)
    ImageView iconCameraImage;

    @Bind(R.id.image_device_state)
    ImageView iconDeviceImage;

    @Bind(R.id.image_phone_state)
    ImageView iconCPhoneImage;

    @Bind(R.id.textView)
    TextView stateTextview;

    @Bind(R.id.textView_oview_device)
    TextView stateDeviceTextview;

    @Bind(R.id.button_connect_cam)
    Button buttonConnectCamera;

    @Bind(R.id.button_connect_device)
    Button buttonConnectDevice;

    @Bind(R.id.button_capture)
    ImageView buttonCapture;
    boolean bCmdDisconnect = false;
    boolean bCameraReady;
    boolean bDeviceReady;

    @Override
    protected int getContentResourceId() {
        return R.layout.activity_main_state;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
    }

    /**
     * Activity  initView
     **/
    @Override
    public void initView() {

        actionBar = new ActionBarTop.Builder(this, R.layout.actionbar_menu_bar)
                .setEventListener(this).setDebugMode(false)
                .setBackgroundColor(Color.parseColor("#00000000")).build();

        actionBar.setButtonImage(R.id.btn_left, R.drawable.icon_back);

        stateTextview.setOnClickListener(this);
        buttonCapture.setOnClickListener(this);
        iconDeviceImage.setOnClickListener(this);
        buttonConnectCamera.setOnClickListener(this);
        buttonConnectDevice.setOnClickListener(this);
        buttonCapture.setEnabled(false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!bCmdDisconnect)
            presenter.ConnectToPrivusWifiSSID();

        if (!AppManager.isDevelopCameraOnlyDebugMode()) {
            presenter.ConnectToOviewDevice();
        }
    }

    @NonNull
    @Override
    public MainStatePresenter createPresenter() {
        return new MainStatePresenterImp(this);
    }

    @NonNull
    @Override
    public ViewState<ViewMainState> createViewState() {
        return new AppMainViewState();
    }

    @Override
    public void onNewViewStateInstance() {

    }

    @Override
    public void onClick(View view) {

        if (view.equals(buttonCapture)) {
            navigator.navigateToCameraView();
        }

        if (view.equals(buttonConnectCamera)) {

            if (!bCameraReady) {
                bCmdDisconnect = false;
                navigator.navigateToScanView();
            } else {
                bCmdDisconnect = true;
                presenter.onDisconnectWifi();
            }
        }

        if (view.equals(buttonConnectDevice)) {
            presenter.ConnectToOviewDevice();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String QrCode = "";
        String password = "";

        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            QrCode = bundle.getString("QR");
            password = bundle.getString("PWD");
        }
    }

    @Override
    public void setCameraState(boolean bSuccess) {

        bCameraReady = bSuccess;
        upDateUIStates();
    }

    @Override
    public void setDeviceState(boolean bSuccess) {
        bDeviceReady = bSuccess;
        upDateUIStates();
    }

    private void upDateUIStates() {

        //Button buttonConnectDevice;
        if (bCameraReady) {
            iconCameraImage.setImageResource(R.drawable.camera_w);
            stateTextview.setText("Connected");
            buttonConnectCamera.setText(AppResource.getString(R.string.disconnect_to_camera));
            buttonConnectCamera.setEnabled(true);
        } else {

            iconCameraImage.setImageResource(R.drawable.camera_g);
            buttonConnectCamera.setText(AppResource.getString(R.string.connect_to_camera));
            stateTextview.setText("Disconnected");
            buttonConnectCamera.setEnabled(true);
        }

        if (bDeviceReady) {
            iconDeviceImage.setImageResource(R.drawable.machine_w);
            stateDeviceTextview.setText("Connected");
            buttonConnectDevice.setEnabled(false);
            buttonConnectDevice.setVisibility(View.INVISIBLE);
            //navigator.navigateToCameraView();
        } else {
            iconDeviceImage.setImageResource(R.drawable.machine_g);
            stateDeviceTextview.setText("Disconnected");
            buttonConnectDevice.setVisibility(View.VISIBLE);
            buttonConnectDevice.setEnabled(true);
        }

        if (AppManager.isDevelopCameraOnlyDebugMode()) {
            bDeviceReady = true;
        }

        if (bCameraReady && bDeviceReady) {
            iconCPhoneImage.setImageResource(R.drawable.phone_w);
            buttonCapture.setEnabled(true);
        } else {
            iconCPhoneImage.setImageResource(R.drawable.phone_g);
            buttonCapture.setEnabled(false);
        }
    }

    @Override
    public void onActionButtonClicked(View view) {

        if (view.getId() == R.id.btn_left) {

            presenter.onUserLogout();
            //navigator.navigateHome();
        }
    }
}
