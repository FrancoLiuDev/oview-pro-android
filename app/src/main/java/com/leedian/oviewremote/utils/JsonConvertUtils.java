package com.leedian.oviewremote.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by francoliu on 2017/2/24.
 */

public class JsonConvertUtils {

    public static ArrayList convertJsonToArray(JSONArray jArray ) throws JSONException {

        ArrayList<String> listdata = new ArrayList<String>();

        if (jArray != null) {
            for (int i=0;i<jArray.length();i++){
                listdata.add(jArray.getString(i));
            }
        }

        return listdata;
    }
}
