package com.leedian.oviewremote.presenter.presenterImp;

import rx.Subscriber;
import com.leedian.oviewremote.base.basePresenter.CameraBasePresenter;
import com.leedian.oviewremote.base.baseView.ActivityUIUpdate;
import com.leedian.oviewremote.base.baseView.BaseMvpView;
import com.leedian.oviewremote.model.dataIn.CameraMetaModel;
import com.leedian.oviewremote.presenter.presenterInterface.GalleryPresenter;
import com.leedian.oviewremote.presenter.task.taskImp.ROviewManagerImp;
import com.leedian.oviewremote.presenter.task.taskInterface.ROviewManager;
import com.leedian.oviewremote.utils.subscript.ObservableActionIdentifier;
import com.leedian.oviewremote.utils.thread.JobExecutor;
import com.leedian.oviewremote.utils.thread.TaskExecutor;
import com.leedian.oviewremote.utils.thread.UIThread;
import com.leedian.oviewremote.view.viewInterface.ViewGallery;

/**
 * Gallery Presenter
 */

public class GalleryPresenterImp extends CameraBasePresenter<ViewGallery>
        implements GalleryPresenter {

    ROviewManager mROviewManager = new ROviewManagerImp();

    static public GalleryViewUIUpdate getUIUpdateInstance(ViewGallery view) {
        return new GalleryViewUIUpdate(view);
    }

    @Override
    public void startUploadFiles(String[] files, CameraMetaModel model) {

        Subscriber<ObservableActionIdentifier> subscriber = new Subscriber<ObservableActionIdentifier>() {
            @Override
            public void onStart() {
                request(1);
                showLoadingDialog();
            }

            @Override
            public void onCompleted() {

                showLoadingDialogSuccess();
            }

            @Override
            public void onError(Throwable e) {

                showLoadingDialogError();
                HandleOnException(e);
            }

            @Override
            public void onNext(ObservableActionIdentifier action) {

                getUIUpdateInstance(getView()).updateViewUploadPercent(action.getContent());
                request(1);
            }
        };

        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), mROviewManager.executeUploadROviewInstance(files[0], files, model));
        taskCase.execute(subscriber);
    }

    private static class GalleryViewUIUpdate
            extends ActivityUIUpdate<ViewGallery> {
        final static public int UI_UPDATE_PERCENT = 1;

        String percent;

        public GalleryViewUIUpdate(ViewGallery view) {
            super(view);
        }

        public void updateViewUploadPercent(String percent) {

            setUpdateId(UI_UPDATE_PERCENT);
            this.percent = percent;

            getView().updateViewInMainThread(this);
        }

        @Override
        protected void onUpdateEventChild(BaseMvpView view, int event) {

            ViewGallery liveView = (ViewGallery) view;

            if (liveView == null)
                return;

            switch (event) {
                case UI_UPDATE_PERCENT:
                    liveView.updateUploadPercent(this.percent);
                    break;
            }
        }
    }
}
