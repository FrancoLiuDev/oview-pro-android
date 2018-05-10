package com.leedian.oviewremote.navigator;
import android.app.Activity;
import android.content.Intent;

import com.leedian.oviewremote.view.activity.CameraViewActivity;
import com.leedian.oviewremote.view.activity.MainStateActivity;
import com.leedian.oviewremote.view.activity.QRCodeScanActivity;
import com.leedian.oviewremote.view.activity.UserLoginActivity;

/**
 * AppNavigator
 *
 * @author Franco
 */
public class AppNavigator {

    static public final String BUNDLE_CONTENT_ZIP_KEY     = "BUNDLE_CONTENT_ZIP_KEY";
    static public final String BUNDLE_HAS_PARENT_ACTIVITY = "BUNDLE_HAS_PARENT_ACTIVITY";
    private Activity viewActivity               = null;

    public AppNavigator(Activity activity) {

        viewActivity = activity;
    }

    public Activity getActivity() {

        return viewActivity;
    }

    public void navigateToScanView(){

        Intent intent = new Intent(getActivity(), QRCodeScanActivity.class);
        getActivity().startActivityForResult(intent,0);
    }

    public void navigateToCameraView() {

        Intent intent = new Intent(getActivity(), CameraViewActivity.class);
        getActivity().startActivity(intent);
    }

    public void navigateHome() {

        Intent intent = new Intent(getActivity(), UserLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    public void navigateToMainStateView() {

        Intent intent = new Intent(getActivity(), MainStateActivity.class);
        getActivity().startActivity(intent);
    }
}
