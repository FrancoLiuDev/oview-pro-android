package com.leedian.oviewremote.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by francoliu on 2017/3/21.
 */

public class BlueToothUtils {

    static public boolean isSupportBlueTooth(Context content){
        return content.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }
}
