package com.leedian.oviewremote.view.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import butterknife.Bind;

import com.leedian.oviewremote.AppResource;
import com.leedian.oviewremote.R;
import com.leedian.oviewremote.base.baseView.BaseActivity;
import com.leedian.oviewremote.model.CameraShootingParam;
import com.leedian.oviewremote.model.dataIn.CameraMetaModel;
import com.leedian.oviewremote.presenter.device.LightDirection;
import com.leedian.oviewremote.presenter.presenterImp.CameraPresenterImp;
import com.leedian.oviewremote.presenter.presenterInterface.CameraPresenter;
import com.leedian.oviewremote.utils.CameraStreamSurfaceView;
import com.leedian.oviewremote.utils.SystemUtils;
import com.leedian.oviewremote.utils.viewUtils.ActionBarTop;
import com.leedian.oviewremote.view.states.CameraViewState;
import com.leedian.oviewremote.view.viewInterface.ViewCamera;

public class CameraViewActivity extends BaseActivity<ViewCamera, CameraPresenter>
        implements ViewCamera, View.OnClickListener, CameraStreamSurfaceView.CameraPreviewTouchListener, ActionBarTop.ActionButtonEvent {

    protected ActionBarTop actionBar;

    CameraShootingParam currentCameraParam = null;
    // preview
    @Bind(R.id.live_surfaceView)
    CameraStreamSurfaceView liveSurfaceView;
    @Bind(R.id.button_take_picture)
    ImageView buttonTakePicture;
    @Bind(R.id.button_take_picture2)
    ImageView buttonStopCapture;
    // camera adjust
    @Bind(R.id.textView_value_display)
    TextView textViewValueDisplay;
    @Bind(R.id.button_plus)
    Button buttonPlus;
    @Bind(R.id.button_minus)
    Button buttonMinus;
    // camera zoom adjust
    @Bind(R.id.textView_zoom_value_display)
    TextView textViewZoomValueDisplay;
    @Bind(R.id.zoom_bar)
    RelativeLayout relativeLayoutZoomBar;
    @Bind(R.id.button_zoom_plus)
    Button buttonZoomPlus;
    @Bind(R.id.button_zoom_minus)
    Button buttonZoomMinus;

    @Bind(R.id.textView_display_param)
    TextView textViewDisplayParam;

    @Bind(R.id.control_bar)
    RelativeLayout relativeLayoutControlBar;
    @Bind(R.id.capture_imageView)
    ImageView imageCapture;
    ValueAdjustView controlBarView;
    ZoomAdjustView controlZoomView;
    // light control too bar
    @Bind(R.id.textView_light)
    TextView textButtonLightPower;
    @Bind(R.id.textView_color_control)
    TextView textButtonColorControl;
    @Bind(R.id.textView_bottomstage)
    TextView textViewBottomStage;
    @Bind(R.id.textView_shooting_cuts)
    TextView textViewShootingCuts;

    //camera control panels
    @Bind(R.id.textView_shooting)
    TextView textViewShooting;
    LightControlPanel lightPanelControl = new LightControlPanel();
    LightPowerControlPanel lightSwitchControl = new LightPowerControlPanel();
    LightColorControlPanel lightColorControl = new LightColorControlPanel();
    PhotoTakeCountPanel photoTakeCount = new PhotoTakeCountPanel();
    PhotoTakeTypePanel photoTakeTypePanel = new PhotoTakeTypePanel();
    @Bind(R.id.lighting_control_bar)
    LinearLayout layoutLightControlToolbar;
    @Bind(R.id.lighting_color_panel)
    LinearLayout layoutLightColorPanel;
    @Bind(R.id.lighting_power_panel)
    LinearLayout layoutLightPowerPanel;
    @Bind(R.id.take_count_layout)
    LinearLayout layoutCountPanel;
    @Bind(R.id.take_type_layout)
    LinearLayout layoutTypePanel;

    int currentZoom = 0;

    @Bind(R.id.loadingProgressLayout)
    RelativeLayout layout_loading_content;

    ShootType shootType;
    ShootTakeCount takeCount;
    int takeTestRotate = 0;

    boolean visible = false;

    CameraMetaModel cameraMetaModel = new CameraMetaModel();

    ProgressDialog progressDialog = new ProgressDialog(this);
    PowerManager.WakeLock wl = null;

    @Override
    protected void onInvalidView() {

        safeRelease();

        if (visible) {
            this.finish();
        }

        //this.finish();
    }

    @Override
    protected int getContentResourceId() {
        return R.layout.activity_camera_view;
    }

    @Override
    protected int getToastActivityContainer() {

        return R.layout.activity_camera_view;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVal();
        initView();
    }

    @Override
    protected void initVal() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        visible = true;
        startFetch();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onPause() {

        visible = false;
        presenter.onStopPreview();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (wl != null) {
            SystemUtils.releasePowerWake(wl);
        }
    }

    @NonNull
    @Override
    public CameraPresenter createPresenter() {
        return new CameraPresenterImp();
    }

    @NonNull
    @Override
    public ViewState<ViewCamera> createViewState() {
        return new CameraViewState();
    }

    @Override
    public void onNewViewStateInstance() {

    }

    @Override
    public void initView() {

        wl = SystemUtils.acquirePowerWake(this, "camera");

        // screen and CPU will stay awake during this section

        actionBar = new ActionBarTop.Builder(this, R.layout.actionbar_camera_bar)
                .setEventListener(this).setDebugMode(false)
                .setBackgroundColor(Color.parseColor("#FF000000")).build();

        controlBarView = new ValueAdjustView(relativeLayoutControlBar);
        controlZoomView = new ZoomAdjustView();
        buttonTakePicture.setOnClickListener(this);

        textButtonColorControl.setOnClickListener(this);
        textButtonLightPower.setOnClickListener(this);
        textViewBottomStage.setOnClickListener(this);
        textViewShootingCuts.setOnClickListener(this);
        textViewShooting.setOnClickListener(this);
        buttonStopCapture.setOnClickListener(this);
        liveSurfaceView.setPreviewTouchListener(this);

        lightPanelControl.init();
        lightSwitchControl.init();
        lightColorControl.init();
        photoTakeCount.init();
        photoTakeTypePanel.init();

        controlBarView.hide();
        controlZoomView.hide();

        layout_loading_content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return true;
            }
        });
    }

    @Override
    protected void hideActivityLoading() {
        super.hideActivityLoading();

        layout_loading_content.setVisibility(View.GONE);
    }

    @Override
    protected void showActivityLoading() {

        layout_loading_content.setVisibility(View.VISIBLE);
    }

    @Override
    public void startFetch() {

        presenter.ConnectToPreviousWifiSSID();
    }

    @Override
    public void updateLiveViewBitmap(Bitmap bitmap) {
        liveSurfaceView.drawFrame(bitmap);
    }

    @Override
    public void updateCameraParam(CameraShootingParam param) {
        currentCameraParam = param;

        loadCameraParam();
    }

    @Override
    public void updateCaptureBitmap(Bitmap captureBitmap) {

        imageCapture.setImageBitmap(captureBitmap);
    }

    @Override
    public void updateCameraZoom(int zoom) {

        currentZoom = zoom;
        cameraMetaModel.syncCameraFocus(String.valueOf(zoom));

        controlZoomView.updateZoomValue(String.valueOf(zoom));
        controlZoomView.show();
    }

    @Override
    public void updateDownloadPercent(int total, int complete) {

        progressDialog.Show();
        progressDialog.setProgress(complete, total);
    }

    @Override
    public void dismissDownloadPercent() {

        progressDialog.Hide();
    }

    private void safeRelease() {

        if (progressDialog != null) {
            progressDialog.Hide();
        }

        dismissDownloadPercent();
    }

    @Override
    public void navigateShowResultActivity() {
        startGalleryActivity();
    }

    @Override
    public void updateFNumberList(ArrayList camAvailableFNumber, String FNumber) {

        if (currentCameraParam == null)
            return;

        currentCameraParam.setCamAvailableFNumber(camAvailableFNumber);
        currentCameraParam.setFNumber(FNumber);

        loadCameraParam();
    }

    private void loadCameraParam() {

        String display = "ISO" + currentCameraParam.getExpISO() + " " +
                "A:" + currentCameraParam.getExpFNumber() + " " +
                "S:" + currentCameraParam.getExpShortcut();

        cameraMetaModel.syncCameraParam(currentCameraParam.getExpISO(), currentCameraParam.getExpShortcut(), currentCameraParam.getExpFNumber());

        textViewDisplayParam.setText(display);
        controlBarView.updateValue(currentCameraParam);
    }

    @Override
    public void onClick(View v) {

        if (v.equals(buttonTakePicture)) {
            //startFetch();
            int cnt = getCaptureCount();
            int type = getCaptureType();

            if (type == 0) {
                presenter.onStartCapture(cnt);
                cameraMetaModel.syncMo(cnt);
            }
            if (type == 1) {
                presenter.onStartCapture(4);
                cameraMetaModel.syncMo(4);
            }
            if (type == 2) {
                presenter.onStartOneCapture(takeTestRotate);
                cameraMetaModel.syncMo(1);
            }

            return;
        }

        if (v.equals(buttonStopCapture)) {

            startGalleryActivity();

            return;
        }

        if (v.equals(textViewShooting)) {
            lightPanelControl.switchPanelTakeType();
        }

        if (v.equals(textViewShootingCuts)) {
            lightPanelControl.switchPanelTakeCount();
        }

        if (v.equals(textButtonLightPower)) {
            lightPanelControl.switchPanelPower();
        }

        if (v.equals(textButtonColorControl)) {

            lightPanelControl.switchPanelColor();
        }
    }

    public void stopCapture() {

        presenter.onStopCapture();
    }

    public void startGalleryActivity() {

        int cnt = getCaptureCount();

        if (getCaptureType() == 1) {
            cnt = 4;
        }

        if (getCaptureType() == 2) {
            cnt = 1;
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(GalleryActivity.EXTRA_CAMERA_PARAM, cameraMetaModel);

        Intent intent = new Intent(this, GalleryActivity.class);
        intent.putExtra(GalleryActivity.EXTRA_NAME, cnt);
        intent.putExtras(bundle);

        startActivityForResult(intent, 23);
    }

    @Override
    public void onTouchPosition(float xPosPercent, float yPosPercent) {
        presenter.onPreviewTouchFocus(xPosPercent, yPosPercent);
    }

    private void onColorOk(int R, int G, int B) {

        presenter.onAdjustDeviceColor(LightDirection.ALL, R, G, B);
        lightPanelControl.init();
    }

    private void onColorChange(int R, int G, int B) {

        presenter.onAdjustDeviceColor(LightDirection.ALL, R, G, B);
    }

    private void onColorCancel() {
        lightPanelControl.init();
    }

    public void onSwitchCancel() {
        lightPanelControl.init();
    }

    public void onSwitchChange(LightDirection direction, boolean enable) {

        if (enable) {
            presenter.onAdjustDeviceColor(direction, 255, 255, 255);
        }

        if (!enable) {
            presenter.onAdjustDeviceColor(direction, 0, 0, 0);
        }
    }

    public void onSwitchOk() {
        lightPanelControl.init();
    }

    public void onTypeOk(ShootType type) {

        shootType = type;

        if (shootType == ShootType.take_default) {
            textViewShooting.setText(AppResource.getString(R.string.take_type_default));
        } else if (shootType == ShootType.take_test) {
            textViewShooting.setText(AppResource.getString(R.string.take_type_test));
        } else if (shootType == ShootType.take_one) {
            textViewShooting.setText(AppResource.getString(R.string.take_type_one));
        }

        if (shootType == ShootType.take_one) {

            Intent intent = new Intent(this, TakeSampleRotateActivity.class);
            startActivityForResult(intent, 22);
        }

        lightPanelControl.init();
    }

    private int getCaptureType() {

        if (shootType == ShootType.take_default) {
            return 0;
        } else if (shootType == ShootType.take_test) {
            return 1;
        } else if (shootType == ShootType.take_one) {
            return 2;
        }
        return 0;
    }

    private int getCaptureCount() {

        if (takeCount == ShootTakeCount.take_4) {
            return 4;
        } else if (takeCount == ShootTakeCount.take_8) {
            return 8;
        } else if (takeCount == ShootTakeCount.take_16) {
            return 16;
        } else if (takeCount == ShootTakeCount.take_32) {
            return 32;
        } else if (takeCount == ShootTakeCount.take_48) {
            return 48;
        }
        return 48;
    }

    public void onTakeCountOk(ShootTakeCount count) {

        takeCount = count;

        if (takeCount == ShootTakeCount.take_4) {
            textViewShootingCuts.setText(AppResource.getString(R.string.take_fore));
        } else if (takeCount == ShootTakeCount.take_8) {
            textViewShootingCuts.setText(AppResource.getString(R.string.take_eight));
        } else if (takeCount == ShootTakeCount.take_16) {
            textViewShootingCuts.setText(AppResource.getString(R.string.take_sixtheen));
        } else if (takeCount == ShootTakeCount.take_32) {
            textViewShootingCuts.setText(AppResource.getString(R.string.take_thirdtwo));
        } else if (takeCount == ShootTakeCount.take_48) {
            textViewShootingCuts.setText(AppResource.getString(R.string.take_foreright));
        }

        lightPanelControl.init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (22): {
                if (resultCode == Activity.RESULT_OK) {

                    takeTestRotate = data.getIntExtra("degree", 0);
                }
                break;
            }

            case (23): {
                if (resultCode != Activity.RESULT_OK) {
                    finish();
                }

                break;
            }
        }
    }

    @Override
    public void onActionButtonClicked(View view) {

        if (view.getId() == R.id.textView_lens) {

            controlBarView.hide();

            controlZoomView.setAdjustModeValue(String.valueOf(currentZoom));
            controlZoomView.show();
            return;
        }

        controlZoomView.hide();

        if (view.getId() == R.id.textView_iso) {
            controlBarView.setAdjustModeValue("iso", currentCameraParam.getCamAvailableIsoSpeedRate(), currentCameraParam.getExpISO());
            controlBarView.show();
        }

        if (view.getId() == R.id.textView_fnumber) {

            controlBarView.setAdjustModeValue("fnumber", currentCameraParam.getCamAvailableFNumber(), currentCameraParam.getExpFNumber());
            controlBarView.show();
        }

        if (view.getId() == R.id.textView_shortcut) {

            controlBarView.setAdjustModeValue("shortcut", currentCameraParam.getCamAvailableShortcut(), currentCameraParam.getExpShortcut());
            controlBarView.show();
        }
    }

    enum ShootType {
        take_default,
        take_test,
        take_one

    }

    enum ShootTakeCount {
        take_4,
        take_8,
        take_16,
        take_32,
        take_48

    }

    private class ZoomAdjustView implements View.OnClickListener {

        //ArrayList zoomList = new ArrayList();

        private String strValue;

        ZoomAdjustView() {

            buttonZoomPlus.setOnClickListener(this);
            buttonZoomMinus.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (view.equals(buttonZoomPlus)) {

                presenter.onChangeZoomPosition("in");
            }

            if (view.equals(buttonZoomMinus)) {

                presenter.onChangeZoomPosition("out");
            }
        }

        void updateZoomValue(String strValue) {
            setAdjustModeValue(strValue);
        }

        void show() {

            relativeLayoutZoomBar.setVisibility(View.VISIBLE);
            textViewZoomValueDisplay.setText(strValue);
        }

        void setAdjustModeValue(String strCurrentValue) {

            strValue = strCurrentValue;
        }

        void hide() {
            relativeLayoutZoomBar.setVisibility(View.GONE);
        }
    }

    private class LightPowerControlPanel implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        Switch switchBackgroundTop;
        TextView textbutton_confirm;
        TextView textbutton_cancel;

        void init() {

            textbutton_confirm = (TextView) layoutLightPowerPanel.findViewById(R.id.textbutton_switch_confirm);
            textbutton_confirm.setOnClickListener(this);

            textbutton_cancel = (TextView) layoutLightPowerPanel.findViewById(R.id.textbutton_switch_cancel);
            textbutton_cancel.setOnClickListener(this);

            switchBackgroundTop = (Switch) layoutLightPowerPanel.findViewById(R.id.switch_backgrund_top);
            switchBackgroundTop.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.equals(textbutton_confirm)) {
                onSwitchCancel();
            }

            if (v.equals(textbutton_cancel)) {
                onSwitchOk();
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.equals(switchBackgroundTop)) {
                onSwitchChange(LightDirection.BACKGROUND_TOP, isChecked);
            }
        }
    }

    private class LightColorControlPanel implements View.OnClickListener {

        TextView textbutton_confirm;
        TextView textbutton_cancel;
        ImageView imageColorPicker;
        ImageView imageColorLightPicker;
        ImageView imageColorPicked;

        int pixelColor;
        int ResultColor;
        float garyPercent;

        boolean isUpdate = false;

        View.OnTouchListener colorTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x;
                float y;

                Matrix inverse = new Matrix();
                imageColorPicker.getImageMatrix().invert(inverse);
                float[] touchPoint = new float[]{event.getX(), event.getY()};
                inverse.mapPoints(touchPoint);

                x = (int) touchPoint[0];
                y = 10;

                if (x <= 10) {
                    x = 10;
                }

                Bitmap bitmap = ((BitmapDrawable) imageColorPicker.getDrawable()).getBitmap();
                int pixel = bitmap.getPixel((int) x, (int) y);

                upDatePixColor(pixel);

                return true;
            }
        };

        View.OnTouchListener garyTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                float x;

                float percent;

                Matrix inverse = new Matrix();
                imageColorLightPicker.getImageMatrix().invert(inverse);
                float[] touchPoint = new float[]{event.getX(), event.getY()};
                inverse.mapPoints(touchPoint);

                x = (int) touchPoint[0];
                if (x <= 10) {
                    x = 10;
                }

                Bitmap bitmap = ((BitmapDrawable) imageColorPicker.getDrawable()).getBitmap();

                percent = x / bitmap.getWidth();

                upDatePixPercent(percent);

                return true;
            }
        };

        private void upDatePixColor(int color) {

            isUpdate = true;
            garyPercent = (float) 0.65;

            int redValue = (int) (Color.red(color) * garyPercent);
            int blueValue = (int) (Color.blue(color) * garyPercent);
            int greenValue = (int) (Color.green(color) * garyPercent);

            ResultColor = getIntFromColor(redValue, greenValue, blueValue);
            pixelColor = color;

            onColorChange(redValue, greenValue, blueValue);

            Bitmap bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            canvas.drawColor(pixelColor);
            imageColorPicked.setImageBitmap(bmp);
        }

        private void upDatePixPercent(float percent) {

            isUpdate = true;
            ResultColor = (int) (pixelColor * percent);

            int redValue = (int) (Color.red(pixelColor) * percent);
            int blueValue = (int) (Color.blue(pixelColor) * percent);
            int greenValue = (int) (Color.green(pixelColor) * percent);

            ResultColor = getIntFromColor(redValue, greenValue, blueValue);

            onColorChange(redValue, greenValue, blueValue);

            Bitmap bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            canvas.drawColor(ResultColor);
            imageColorPicked.setImageBitmap(bmp);
        }

        int getIntFromColor(int Red, int Green, int Blue) {
            Red = (Red << 16) & 0x00FF0000;
            Green = (Green << 8) & 0x0000FF00;
            Blue = Blue & 0x000000FF;

            return 0xFF000000 | Red | Green | Blue;
        }

        void init() {

            textbutton_confirm = (TextView) layoutLightColorPanel.findViewById(R.id.textbutton_color_confirm);
            textbutton_confirm.setOnClickListener(this);

            textbutton_cancel = (TextView) layoutLightColorPanel.findViewById(R.id.textbutton_color_cancel);
            textbutton_cancel.setOnClickListener(this);

            imageColorPicker = (ImageView) layoutLightColorPanel.findViewById(R.id.image_color_picker);
            imageColorPicker.setOnTouchListener(colorTouchListener);

            imageColorPicker = (ImageView) layoutLightColorPanel.findViewById(R.id.image_color_picker);
            imageColorPicker.setOnTouchListener(colorTouchListener);

            imageColorLightPicker = (ImageView) layoutLightColorPanel.findViewById(R.id.image_gray_picker);
            imageColorLightPicker.setOnTouchListener(garyTouchListener);

            imageColorPicked = (ImageView) layoutLightColorPanel.findViewById(R.id.image_color_picked);
        }

        @Override
        public void onClick(View v) {

            if (v.equals(textbutton_confirm)) {

                if (!isUpdate) {
                    onColorCancel();
                    return;
                }

                int redValue = Color.red(ResultColor);
                int blueValue = Color.blue(ResultColor);
                int greenValue = Color.green(ResultColor);

                onColorOk(redValue, greenValue, blueValue);
            }

            if (v.equals(textbutton_cancel)) {
                onColorCancel();
            }
        }
    }

    private class PhotoTakeTypePanel implements View.OnClickListener {

        ShootType type = ShootType.take_default;

        TextView textbutton_default;
        TextView textbutton_test;
        TextView textbutton_one;

        @Override
        public void onClick(View view) {

            if (view.equals(textbutton_default)) {

                type = ShootType.take_default;
                onTypeOk(type);
            }

            if (view.equals(textbutton_test)) {
                type = ShootType.take_test;
                onTypeOk(type);
            }

            if (view.equals(textbutton_one)) {
                type = ShootType.take_one;
                onTypeOk(type);
            }
        }

        void init() {

            textbutton_default = (TextView) layoutTypePanel.findViewById(R.id.textView_type_default);
            textbutton_default.setOnClickListener(this);

            textbutton_test = (TextView) layoutTypePanel.findViewById(R.id.textView_type_test);
            textbutton_test.setOnClickListener(this);

            textbutton_one = (TextView) layoutTypePanel.findViewById(R.id.textView_type_one);
            textbutton_one.setOnClickListener(this);

            onTypeOk(type);
        }
    }

    private class PhotoTakeCountPanel implements View.OnClickListener {

        ShootTakeCount count = ShootTakeCount.take_48;

        TextView textbutton_4;
        TextView textbutton_8;
        TextView textbutton_16;
        TextView textbutton_32;
        TextView textbutton_48;

        @Override
        public void onClick(View view) {

            if (view.equals(textbutton_4)) {

                count = ShootTakeCount.take_4;
                onTakeCountOk(count);
            }

            if (view.equals(textbutton_8)) {
                count = ShootTakeCount.take_8;
                onTakeCountOk(count);
            }

            if (view.equals(textbutton_16)) {
                count = ShootTakeCount.take_16;
                onTakeCountOk(count);
            }

            if (view.equals(textbutton_32)) {
                count = ShootTakeCount.take_32;
                onTakeCountOk(count);
            }

            if (view.equals(textbutton_48)) {
                count = ShootTakeCount.take_48;
                onTakeCountOk(count);
            }
        }

        void init() {

            textbutton_4 = (TextView) layoutCountPanel.findViewById(R.id.textView_count_4);
            textbutton_4.setOnClickListener(this);

            textbutton_8 = (TextView) layoutCountPanel.findViewById(R.id.textView_count_8);
            textbutton_8.setOnClickListener(this);

            textbutton_16 = (TextView) layoutCountPanel.findViewById(R.id.textView_count_16);
            textbutton_16.setOnClickListener(this);

            textbutton_32 = (TextView) layoutCountPanel.findViewById(R.id.textView_count_32);
            textbutton_32.setOnClickListener(this);

            textbutton_48 = (TextView) layoutCountPanel.findViewById(R.id.textView_count_48);
            textbutton_48.setOnClickListener(this);

            onTakeCountOk(count);
        }
    }

    private class LightControlPanel {

        void switchPanelColor() {
            closeAll();
            layoutLightColorPanel.setVisibility(View.VISIBLE);
        }

        void switchPanelPower() {
            closeAll();
            layoutLightPowerPanel.setVisibility(View.VISIBLE);
        }

        private void closeAll() {

            layoutLightColorPanel.setVisibility(View.GONE);
            layoutLightPowerPanel.setVisibility(View.GONE);
            layoutCountPanel.setVisibility(View.GONE);
            layoutTypePanel.setVisibility(View.GONE);
        }

        void init() {

            closeAll();
        }

        void switchPanelTakeCount() {

            closeAll();
            layoutCountPanel.setVisibility(View.VISIBLE);
        }

        void switchPanelTakeType() {
            closeAll();
            layoutTypePanel.setVisibility(View.VISIBLE);
        }
    }

    private class ProgressDialog implements View.OnClickListener {

        MaterialDialog Dlg = null;
        Context context;

        ProgressBar progressBar = null;
        Button buttonStop = null;
        TextView textProgressPercent = null;

        ProgressDialog(Context context) {
            this.context = context;
        }

        void Show() {

            if (Dlg == null) {
                Dlg = new MaterialDialog.Builder(this.context)
                        .title("")
                        .theme(Theme.LIGHT)
                        .canceledOnTouchOutside(false)
                        .customView(R.layout.view_capture_progress, false)

                        .show();

                textProgressPercent = (TextView) Dlg.getView().findViewById(R.id.text_progress_precent);
                buttonStop = (Button) Dlg.getView().findViewById(R.id.button_stop_capture2);
                progressBar = (ProgressBar) Dlg.getView().findViewById(R.id.progressBar2);
                buttonStop.setOnClickListener(this);
            }
        }

        void setProgress(int percent, int total) {

            float p = (float) percent / total;
            p = p * 100;
            progressBar.setProgress((int) p);
            textProgressPercent.setText(String.valueOf(percent) + "/" + String.valueOf(total));
        }

        void Hide() {

            if (Dlg != null) {
                Dlg.hide();
                Dlg.dismiss();
            }

            Dlg = null;
        }

        @Override
        public void onClick(View v) {

            stopCapture();
        }
    }

    private class ValueAdjustView implements View.OnClickListener {

        private RelativeLayout relativeLayoutControlBar;
        private ArrayList listAdjustValue;
        private String strValue;
        private String strMode;

        ValueAdjustView(RelativeLayout layout) {
            this.relativeLayoutControlBar = layout;

            textViewValueDisplay = (TextView) findViewById(R.id.textView_value_display);
            buttonPlus = (Button) findViewById(R.id.button_plus);
            buttonMinus = (Button) findViewById(R.id.button_minus);

            buttonPlus.setOnClickListener(this);
            buttonMinus.setOnClickListener(this);
        }

        void updateValue(CameraShootingParam model) {

            if (strMode == null) return;

            if (strMode.equals("iso")) {
                setAdjustModeValue(strMode, model.getCamAvailableIsoSpeedRate(), model.getExpISO());
                show();
            }

            if (strMode.equals("fnumber")) {
                setAdjustModeValue(strMode, model.getCamAvailableFNumber(), model.getExpFNumber());
                show();
            }

            if (strMode.equals("shortcut")) {
                setAdjustModeValue(strMode, model.getCamAvailableShortcut(), model.getExpShortcut());
                show();
            }
        }

        void setAdjustModeValue(String mode, ArrayList listValues, String strCurrentValue) {
            listAdjustValue = listValues;
            strValue = strCurrentValue;
            strMode = mode;
        }

        void show() {

            relativeLayoutControlBar.setVisibility(View.VISIBLE);
            textViewValueDisplay.setText(strValue);
        }

        @Override
        public void onClick(View view) {

            if (view.equals(buttonPlus)) {

                String next = findValueNext();
                if (!next.equals(""))
                    presenter.onChangeCameraParamValue(strMode, next);
            }

            if (view.equals(buttonMinus)) {

                String next = findValuePrevious();
                if (!next.equals(""))
                    presenter.onChangeCameraParamValue(strMode, next);
            }
        }

        String findValueNext() {

            for (int i = 0; i < listAdjustValue.size(); i++) {

                String val = listAdjustValue.get(i).toString();

                if (strValue.equals(val)) {

                    if ((i + 1) >= listAdjustValue.size())
                        return "";

                    return listAdjustValue.get(i + 1).toString();
                }
            }
            return "";
        }

        String findValuePrevious() {

            for (int i = 0; i < listAdjustValue.size(); i++) {

                String val = listAdjustValue.get(i).toString();

                if (strValue.equals(val)) {

                    if ((i - 1) < 0)
                        return "";

                    return listAdjustValue.get(i - 1).toString();
                }
            }
            return "";
        }

        void hide() {

            relativeLayoutControlBar.setVisibility(View.GONE);
        }
    }
}
