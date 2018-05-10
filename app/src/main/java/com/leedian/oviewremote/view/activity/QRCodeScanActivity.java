package com.leedian.oviewremote.view.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;

import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import butterknife.Bind;

import com.leedian.oviewremote.R;
import com.leedian.oviewremote.base.baseView.BaseActivity;
import com.leedian.oviewremote.presenter.presenterImp.QRCodeScanPresenterImp;
import com.leedian.oviewremote.presenter.presenterInterface.QRCodeScanPresenter;
import com.leedian.oviewremote.utils.viewUtils.ActionBarTop;
import com.leedian.oviewremote.utils.viewUtils.CameraProvider;
import com.leedian.oviewremote.view.states.QRCodeScanState;
import com.leedian.oviewremote.view.viewInterface.ViewQRCodeScan;

public class QRCodeScanActivity extends BaseActivity<ViewQRCodeScan, QRCodeScanPresenter>
        implements ViewQRCodeScan,
        View.OnClickListener,
        ActionBarTop.ActionButtonEvent,
        CameraProvider.CameraEventHandler {

    /**
     * Top navigation bar
     */
    protected ActionBarTop actionBar;
    /**
     * camera preview container
     */
    @Bind(R.id.layout_scan_container)
    RelativeLayout preview_container_layout;

    /**
     * logo image
     */
    private CameraProvider camera;

    @Override
    protected int getContentResourceId() {
        return R.layout.activity_qrcode_scan;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVal();
        initView();
        initData();
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (camera.isCameraPermission()) {
            startActivityScan();
        } else {
            camera.RequestCameraPermission(CameraProvider.CAMERA_PERMISSIONS_REQUEST_ID);
        }
    }

    public void startActivityScan() {

        camera.initControls();
    }

    @Override
    protected void initView() {

        super.initView();

        actionBar = new ActionBarTop.Builder(this, R.layout.actionbar_menu_bar)
                .setEventListener(this).setDebugMode(false)
                .setBackgroundColor(Color.parseColor("#00000000")).build();

        camera = new CameraProvider.Builder(this, preview_container_layout).setEventHandler(this)
                .Build();
    }

    @NonNull
    @Override
    public QRCodeScanPresenter createPresenter() {
        return new QRCodeScanPresenterImp();
    }

    @NonNull
    @Override
    public ViewState<ViewQRCodeScan> createViewState() {
        return new QRCodeScanState();
    }

    @Override
    public void onNewViewStateInstance() {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        if (requestCode == CameraProvider.CAMERA_PERMISSIONS_REQUEST_ID) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startActivityScan();
            }
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
        camera.releaseCamera();
    }

    @Override
    protected void onPause() {

        super.onPause();
        camera.releaseCamera();
    }

    @Override
    public void onActionButtonClicked(View view) {

    }

    @Override
    public void onReadData(String data) {

        presenter.onScanQRCode(data);
    }

    @Override
    public void setCameraServerFound(String ssid, String password) {

        Intent intent = new Intent();
        Bundle b = new Bundle();

        b.putString("QR", ssid);
        b.putString("PWD", password);

        intent.putExtras(b);
        this.setResult(RESULT_OK, intent);

        finish();
    }

    @Override
    public void onErrorPermission() {

    }
}
