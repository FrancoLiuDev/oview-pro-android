package com.leedian.oviewremote.presenter.task.taskImp;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import com.leedian.oviewremote.AppManager;
import com.leedian.oviewremote.model.dataIn.CameraMetaModel;
import com.leedian.oviewremote.model.restapi.OviewApi;
import com.leedian.oviewremote.model.restapi.OviewUserApi;
import com.leedian.oviewremote.presenter.task.taskInterface.ROviewManager;
import com.leedian.oviewremote.utils.UploadListener;
import com.leedian.oviewremote.utils.exception.DomainException;
import com.leedian.oviewremote.utils.subscript.ObservableActionIdentifier;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.blankj.utilcode.utils.FileUtils.deleteFile;
import static com.blankj.utilcode.utils.FileUtils.isFileExists;
import static com.blankj.utilcode.utils.ZipUtils.zipFiles;

/**
 * Created by francoliu on 2017/4/12.
 */

public class ROviewManagerImp implements ROviewManager {




    public static void zip(String[] files, String zipFile) throws IOException {
        int BUFFER_SIZE = 3600;

        BufferedInputStream origin = null;
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        try {
            byte data[] = new byte[BUFFER_SIZE];

            for (int i = 0; i < files.length; i++) {
                FileInputStream fi = new FileInputStream(files[i]);
                origin = new BufferedInputStream(fi, BUFFER_SIZE);
                try {
                    ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));

                    //entry.setMethod(ZipEntry.DEFLATED);

                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                        out.write(data, 0, count);
                    }
                }
                finally {
                    origin.close();
                }
            }
        }
        finally {
            out.close();
        }
    }

    @Override
    public Observable<ObservableActionIdentifier> executeUploadROviewInstance(final String cover, final String[] files, final CameraMetaModel model) {

        return Observable.create(
                new Observable.OnSubscribe<ObservableActionIdentifier>() {
                    @Override
                    public void call(final Subscriber<? super ObservableActionIdentifier> subscriber) {

                        boolean      isSuccess;
                        OviewApi HttpApi = new OviewApi();
                        OviewUserApi userApi = new OviewUserApi();

                        try {


                            String zipFile = AppManager.getOviewContentDir()+ "/R360.zip";

                            //if (isFileExists(zipFile)){
                                //zip (files,zipFile);
                            List<File> list = new ArrayList<File>();

                            for(int i = 0 ;i<files.length;i++){
                                File file = new File(files[i]);

                                list.add(file);
                            }
                            zipFiles(list,zipFile);
                            //}


                            isSuccess = userApi.checkAuthToken();
                            if (!isSuccess) {
                                isSuccess = userApi.requestExchangeToken();
                            }

                            if (!isSuccess){
                                Exception exception = DomainException
                                        .buildHttpException(HttpApi.getMajor_code(), HttpApi
                                                .getMinor_code());
                                subscriber.onError(exception);
                                return;
                            }

                            isSuccess = HttpApi.createROviewInstance(cover,zipFile,model,new UploadListener(){

                                @Override
                                public void onProgress(int progress) {

                                    ObservableActionIdentifier observableActionIdentifier = new ObservableActionIdentifier("progress",String.valueOf(progress));
                                    subscriber.onNext(observableActionIdentifier);
                                }
                            });


                            if (!isSuccess) {
                                Exception exception = DomainException
                                        .buildHttpException(HttpApi.getMajor_code(), HttpApi
                                                .getMinor_code());
                                subscriber.onError(exception);
                                return;
                            }



                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(DomainException.getDomainException(e));
                        }
                    }
                }).onBackpressureDrop().sample(500, TimeUnit.MILLISECONDS)
                .delay(1, TimeUnit.MILLISECONDS, Schedulers.immediate());
    }
}
