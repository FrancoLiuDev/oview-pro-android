package com.leedian.oviewremote.utils;

import android.app.Activity;
import android.os.PowerManager;
import android.os.SystemClock;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by francoliu on 2017/4/25.
 */

public class SystemUtils {

    public  static PowerManager.WakeLock acquirePowerWake(Activity activity ,String tag){

          PowerManager powerManager = (PowerManager) activity.getSystemService(POWER_SERVICE);

          PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,

                  tag);

         wakeLock.acquire();

         return wakeLock;
    }

    public  static void releasePowerWake(PowerManager.WakeLock lock){

        if (lock.isHeld())
          lock.release();
      }

    public  static void acquireDelay(long ms){

        long start = SystemClock.uptimeMillis();
        long end = SystemClock.uptimeMillis();

        while (true){

            end = SystemClock.uptimeMillis();

            if ((end - start) >= ms){
                break;
            }
        }
    }


}
