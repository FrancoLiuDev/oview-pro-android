package com.leedian.oviewremote.model.restapi;

import com.leedian.oviewremote.AppResource;
import com.leedian.oviewremote.R;

/**
 * Oview Url Constants
 *
 * @author Franco
 */
public class UrlMessagesConstants {
    static String StrHttpServiceTripleDesKey = null;

    static String StrHttpServiceBrandName = null;

    private static String StrHttpServiceUrl = null;

    private static String IDENTIFIER_OVIEW_API_ROOT = "api/v1/";

    static String IDENTIFIER_CONTENT_PACK_LIST = IDENTIFIER_OVIEW_API_ROOT + "h360";

    static String IDENTIFIER_CONTENT_R360 = IDENTIFIER_OVIEW_API_ROOT + "r360";

    static String IDENTIFIER_CONTENT_ITEM = IDENTIFIER_OVIEW_API_ROOT + "h360";

    static String IDENTIFIER_OVIEW_INFO = IDENTIFIER_OVIEW_API_ROOT + "infonode";

    static String IDENTIFIER_OVIEW_LOGIN = IDENTIFIER_OVIEW_API_ROOT + "user/login";

    static String IDENTIFIER_OVIEW_LOGOUT = IDENTIFIER_OVIEW_API_ROOT + "user/logout";


    static String IDENTIFIER_OVIEW_RELOG = IDENTIFIER_OVIEW_API_ROOT + "user/relog";

    static String IDENTIFIER_OVIEW_AUTH = IDENTIFIER_OVIEW_API_ROOT + "user/auth";
    static String IDENTIFIER_OVIEW_360FILE = "/fp";

    /**
     * init
     *
     */
    public static void init() {

        initIMServerUrl();
    }

    /**
     * init Server Url
     */
    private static void initIMServerUrl() {

        StrHttpServiceTripleDesKey = AppResource.getString(R.string.triple_des_key);
        StrHttpServiceBrandName = AppResource.getString(R.string.brand_name);
    }


    /**
     * set http server root
     *
     * @param strHttpServiceRoot root
     */
    public static void setStrHttpServiceRoot(String strHttpServiceRoot) {

        StrHttpServiceUrl = strHttpServiceRoot;
    }

    /**
     * Get Request Rest Url path string
     *
     * @param withQuestion  If Question mark follow
     * @param identifier rest path id
     * @return
     */
    static String getRequestRestUrl(boolean withQuestion, String identifier) {

        return (withQuestion) ?
                ("/" + identifier + "?") :
                ("/" + identifier);
    }

    /**
     * Get File Download Rest Url
     *
     * @param file path
     * @return String
     */
    public static String getFileDownloadRestUrl(String file) {

        String IDENTIFIER_FILE = "fp";
        return strHttpServiceRoot() + "/" + IDENTIFIER_OVIEW_API_ROOT + IDENTIFIER_FILE + "/" + file;
    }

    /**
     * Get Http Service Root String
     *
     * @return String
     */
    static String strHttpServiceRoot() {

        return "http://" + StrHttpServiceUrl + ":18000";
    }
}
