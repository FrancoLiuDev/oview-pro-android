package com.leedian.oviewremote.view.activity;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wang.avi.AVLoadingIndicatorView;
import butterknife.Bind;

import com.leedian.oviewremote.AppManager;
import com.leedian.oviewremote.R;
import com.leedian.oviewremote.base.baseView.BaseActivity;
import com.leedian.oviewremote.model.dataIn.CameraMetaModel;
import com.leedian.oviewremote.presenter.presenterImp.GalleryPresenterImp;
import com.leedian.oviewremote.presenter.presenterInterface.GalleryPresenter;
import com.leedian.oviewremote.utils.SystemUtils;
import com.leedian.oviewremote.utils.viewUtils.ActionBarTop;
import com.leedian.oviewremote.view.states.GalleryViewState;
import com.leedian.oviewremote.view.viewInterface.ViewGallery;

public class GalleryActivity extends BaseActivity<ViewGallery, GalleryPresenter>
        implements ViewGallery, View.OnClickListener, ActionBarTop.ActionButtonEvent {
    public static final String TAG = "GalleryActivity";
    public static final String EXTRA_NAME = "images";
    public static final String EXTRA_CAMERA_PARAM = "CAMERA";

    protected ActionBarTop actionBar;
    DisplayImageOptions options;
    CameraMetaModel modelCamera;
    @Bind(R.id.pager)
    ViewPager _pager;
    @Bind(R.id.thumbnails)
    LinearLayout _thumbnails;
    @Bind(R.id.button_send)
    Button buttonSend;
    @Bind(R.id.loadingProgressLayout)
    RelativeLayout loadingContainerLayout;
    @Bind(R.id.error_content)
    RelativeLayout layout_error_content;
    @Bind(R.id.success_content)
    RelativeLayout layout_success_content;
    @Bind(R.id.loading_content)
    RelativeLayout layout_loading_content;
    @Bind(R.id.button_continue_capture)
    Button buttonContinueCapture;
    @Bind(R.id.button_stop_capture)
    Button buttonStopCapture;
    @Bind(R.id.button_re_upload)
    Button buttonContinueUpload;
    @Bind(R.id.button_stop_upload)
    Button buttonStopUpload;
    @Bind(R.id.textViewUploadPercent)
    TextView textViewPercent;
    @Bind(R.id.avi)
    AVLoadingIndicatorView loadingAnimationView;
    PowerManager.WakeLock wl = null;

    private ArrayList<File> _image_files;

    @Override
    protected int getContentResourceId() {
        return R.layout.activity_gallery;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GalleryPagerAdapter _adapter = new GalleryPagerAdapter(this);
        wl = SystemUtils.acquirePowerWake(this, "Gallery");

        actionBar = new ActionBarTop.Builder(this, R.layout.actionbar_menu_bar)
                .setEventListener(this).setDebugMode(false)
                .setBackgroundColor(Color.parseColor("#00000000")).build();

        actionBar.setButtonImage(R.id.btn_left, R.drawable.icon_undo);

        int cnt = getIntent().getIntExtra(EXTRA_NAME, 4);
        modelCamera = (CameraMetaModel) getIntent().getSerializableExtra(EXTRA_CAMERA_PARAM);

        _image_files = new ArrayList<>();

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));

        new Thread(new Runnable() {

            @Override
            public void run() {
                ImageLoader.getInstance().clearDiskCache();
                ImageLoader.getInstance().clearMemoryCache();
            }
        }).start();

        for (int i = 1; i <= cnt; i++) {

            String filename = String.format("%03d", i);
            File file = new File(AppManager.getOviewContentDir() + "/" + filename + ".jpg");

            _image_files.add(file);
        }

        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading()
                .cacheOnDisc()
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        _pager.setAdapter(_adapter);
        _pager.setOffscreenPageLimit(1); // how many images to load into memory

        buttonSend.setOnClickListener(this);
        buttonStopUpload.setOnClickListener(this);
        buttonContinueUpload.setOnClickListener(this);
        buttonStopCapture.setOnClickListener(this);
        buttonContinueCapture.setOnClickListener(this);

        loadingContainerLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (wl != null) {
            SystemUtils.releasePowerWake(wl);
        }
    }

    @Override
    protected void showActivityLoading() {

        loadingContainerLayout.setVisibility(View.VISIBLE);
        layout_loading_content.setVisibility(View.VISIBLE);
        loadingAnimationView.show();
    }

    @Override
    protected void showActivityLoadingError() {

        resetLoadingContent();
        layout_error_content.setVisibility(View.VISIBLE);
    }

    private void resetLoadingContent() {
        layout_error_content.setVisibility(View.GONE);
        layout_loading_content.setVisibility(View.GONE);
        layout_success_content.setVisibility(View.GONE);
    }

    @Override
    protected void showActivityLoadingSuccess() {

        resetLoadingContent();
        layout_success_content.setVisibility(View.VISIBLE);
    }

    @Override
    protected void hideActivityLoading() {

        resetLoadingContent();
        loadingContainerLayout.setVisibility(View.GONE);
        loadingAnimationView.hide();
    }

    @NonNull
    @Override
    public GalleryPresenter createPresenter() {
        return new GalleryPresenterImp();
    }

    @Override
    public void onClick(View v) {

        if (v.equals(buttonSend)) {

            String[] files = new String[_image_files.size()];

            for (int i = 0; i < _image_files.size(); i++) {

                files[i] = _image_files.get(i).getAbsolutePath();
            }
            textViewPercent.setText("0 %");
            presenter.startUploadFiles(files, modelCamera);
        }

        if (v.equals(buttonStopUpload)) {

            hideLoadingDialog();
        }

        if (v.equals(buttonContinueUpload)) {

            hideLoadingDialog();

            String[] files = new String[_image_files.size()];

            for (int i = 0; i < _image_files.size(); i++) {

                files[i] = _image_files.get(i).getAbsolutePath();
            }

            textViewPercent.setText("0 %");
            presenter.startUploadFiles(files, modelCamera);
        }

        if (v.equals(buttonStopCapture)) {

            finishActivity(false);
        }

        if (v.equals(buttonContinueCapture)) {
            finishActivity(true);
        }
    }

    private void finishActivity(boolean stayCapture) {

        Intent resultIntent = new Intent();

        if (stayCapture)
            setResult(Activity.RESULT_OK, resultIntent);
        else
            setResult(Activity.RESULT_CANCELED, resultIntent);

        finish();
    }

    @NonNull
    @Override
    public ViewState<ViewGallery> createViewState() {
        return new GalleryViewState();
    }

    @Override
    public void onNewViewStateInstance() {

    }

    @Override
    public void onBackPressed() {

        finishActivity(true);

        super.onBackPressed();
    }

    @Override
    public void onActionButtonClicked(View view) {

        if (view.getId() == R.id.btn_left) {
            finishActivity(true);
        }
    }

    @Override
    public void updateUploadPercent(String percent) {

        textViewPercent.setText(percent + " %");
    }

    class GalleryPagerAdapter extends PagerAdapter {

        Context _context;
        LayoutInflater _inflater;

        ImageLoader imageLoader;

        GalleryPagerAdapter(Context context) {
            _context = context;
            _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            imageLoader = ImageLoader.getInstance();
        }

        @Override
        public int getCount() {
            return _image_files.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            View itemView = _inflater.inflate(R.layout.pager_gallery_item, container, false);
            container.addView(itemView);

            final SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) itemView.findViewById(R.id.image);
            // Get the border size to show around each image

            // Set the thumbnail layout parameters. Adjust as required

            // You could also set like so to remove borders
            //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
            //        ViewGroup.LayoutParams.WRAP_CONTENT,
            //        ViewGroup.LayoutParams.WRAP_CONTENT);

            String decodedImgUri = Uri.fromFile(_image_files.get(position)).toString();

            imageLoader.loadImage(decodedImgUri, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    imageView.setImage(ImageSource.bitmap(loadedImage));
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //_thumbnails.removeViewAt(position);
            container.removeView((LinearLayout) object);
        }
    }
}
