package com.leedian.oviewremote.base.baseView;
import android.os.Bundle;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;

import butterknife.ButterKnife;
import icepick.Icepick;

/**
 * MvpViewStateActivity for Mvc ViewState in this App
 *
 * This MvpViewStateActivity is used as Base MvpViewStateActivity .
 *
 * @author Franco
 */

public abstract class BaseViewStateActivity<V extends MvpView, P extends MvpPresenter<V>>
        extends MvpViewStateActivity<V, P>
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        injectDependencies();
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this,
                                     savedInstanceState);
    }

    @Override
    public void onContentChanged() {

        super.onContentChanged();
        ButterKnife.bind(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this,
                                  outState);
    }

    protected void injectDependencies() {

    }
}
