package com.leedian.oviewremote.view.states;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;

import com.leedian.oviewremote.view.viewInterface.ViewCamera;
import com.leedian.oviewremote.view.viewInterface.ViewGallery;

/**
 * Created by francoliu on 2017/2/7.
 */

public class GalleryViewState implements RestorableViewState<ViewGallery> {
    @Override
    public void saveInstanceState(@NonNull Bundle out) {

    }

    @Override
    public RestorableViewState<ViewGallery> restoreInstanceState(Bundle in) {
        return null;
    }

    @Override
    public void apply(ViewGallery view, boolean retained) {

    }
}
