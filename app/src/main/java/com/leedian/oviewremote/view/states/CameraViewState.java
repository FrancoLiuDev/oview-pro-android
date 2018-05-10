package com.leedian.oviewremote.view.states;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;
import com.leedian.oviewremote.view.viewInterface.ViewCamera;

/**
 * Created by francoliu on 2017/2/7.
 */

public class CameraViewState implements RestorableViewState<ViewCamera> {
    @Override
    public void saveInstanceState(@NonNull Bundle out) {

    }

    @Override
    public RestorableViewState<ViewCamera> restoreInstanceState(Bundle in) {
        return null;
    }

    @Override
    public void apply(ViewCamera view, boolean retained) {

    }
}
