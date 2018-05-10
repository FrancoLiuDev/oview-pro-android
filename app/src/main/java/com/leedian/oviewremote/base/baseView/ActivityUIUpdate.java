package com.leedian.oviewremote.base.baseView;
import android.support.annotation.Nullable;
import java.lang.ref.WeakReference;

/**
 * ActivityUIUpdate
 *
 * @author Franco
 */
public class ActivityUIUpdate<V extends BaseMvpView>
        implements Runnable
{
    final static public int UI_DOMAIN_ERROR             = 50;
    final static public int UI_UPDATE_SHOW_ERROR_STRING = UI_DOMAIN_ERROR + 0;
    final static public int UI_UPDATE_ON_INVALID_VIEW   = UI_DOMAIN_ERROR + 1;
    final static public int UI_UPDATE_SHOW_LOADING      = UI_DOMAIN_ERROR + 5;
    final static public int UI_UPDATE_HIDE_LOADING      = UI_DOMAIN_ERROR + 6;
    final static public int UI_SHOW_TIP_MASSAGE         = UI_DOMAIN_ERROR + 7;
    final static public int UI_UPDATE_SHOW_LOADING_MSG      = UI_DOMAIN_ERROR + 8;

    final static public int UI_UPDATE_SHOW_LOADING_ERROR      = UI_DOMAIN_ERROR + 9;
    final static public int UI_UPDATE_SHOW_LOADING_SUCEESS      = UI_DOMAIN_ERROR + 10;

    public void setUpdateId(int updateId) {
        UpdateId = updateId;
    }

    private int              UpdateId;
    private String msg;
    private WeakReference<V> viewRef;
    onUpdateEvent event = new onUpdateEvent() {
        @Override
        public void onUpdateEvent(int event) {

            BaseMvpView view = getView();
            if (view == null) { return; }

            switch (event) {
                case UI_UPDATE_SHOW_ERROR_STRING:
                    view.displayErrorResponse(msg);
                    break;
                case UI_SHOW_TIP_MASSAGE:
                    view.displayTipMassage(msg);
                    break;
                case UI_UPDATE_ON_INVALID_VIEW:
                    view.reactiveAsInvalidView();
                    break;
                case UI_UPDATE_SHOW_LOADING:
                    view.showLoadingDialog();
                    break;

                case UI_UPDATE_SHOW_LOADING_ERROR:
                    view.showLoadingDialogError();
                    break;

                case UI_UPDATE_SHOW_LOADING_SUCEESS:
                    view.showLoadingDialogSuccess();
                    break;

                case UI_UPDATE_SHOW_LOADING_MSG:
                    view.showLoadingDialogMsg(msg);
                    break;
                case UI_UPDATE_HIDE_LOADING:
                    view.hideLoadingDialog();
                    break;
            }
            onUpdateEventChild(view, event);
        }
    };

    public ActivityUIUpdate(int id , V view) {

        this.UpdateId = id;
        viewRef = new WeakReference<V>(view);
    }

    public ActivityUIUpdate( V view) {


        viewRef = new WeakReference<V>(view);
    }

    public ActivityUIUpdate setShowMsg(String msg) {

        this.msg = msg;
        return this;
    }

    @Nullable
    public V getView() {

        return viewRef == null ?
                null :
                viewRef.get();
    }

    protected void onUpdateEventChild(BaseMvpView view, int event) {

    }

    public void setEventListener(onUpdateEvent event) {

        this.event = event;
    }

    public void setViewAsInvalid(){

        BaseMvpView view = getView();

        if (view ==null){
            return;
        }

        setUpdateId(UI_UPDATE_ON_INVALID_VIEW);
        view.updateViewInMainThread(this);
    }


    public void showErrorMessage(String msg){

        BaseMvpView view = getView();

        if (view ==null){
            return;
        }


        setUpdateId(UI_UPDATE_SHOW_ERROR_STRING);
        this.msg = msg;

        view.updateViewInMainThread(this);
    }



    public void showLoadingDialog(){

        BaseMvpView view = getView();

        if (view ==null){
            return;
        }


        setUpdateId(UI_UPDATE_SHOW_LOADING);
        view.updateViewInMainThread(this);
    }

    public void showLoadingDialogError(){

        BaseMvpView view = getView();

        if (view ==null){
            return;
        }


        setUpdateId(UI_UPDATE_SHOW_LOADING_ERROR);
        view.updateViewInMainThread(this);
    }

    public void showLoadingDialogSuccess(){

        BaseMvpView view = getView();

        if (view ==null){
            return;
        }


        setUpdateId(UI_UPDATE_SHOW_LOADING_SUCEESS);
        view.updateViewInMainThread(this);
    }

    public void showLoadingDialogMsg(String msg){

        BaseMvpView view = getView();

        if (view ==null){
            return;
        }

        this.msg = msg;
        setUpdateId(UI_UPDATE_SHOW_LOADING_MSG);
        view.updateViewInMainThread(this);
    }

    public void hideLoadingDialog(){

        BaseMvpView view = getView();

        if (view ==null){
            return;
        }

        setUpdateId(UI_UPDATE_HIDE_LOADING);
        view.updateViewInMainThread(this);
    }
    @Override
    public void run() {

        event.onUpdateEvent(UpdateId);
    }

    public interface onUpdateEvent {
        void onUpdateEvent(int event);
    }
}
