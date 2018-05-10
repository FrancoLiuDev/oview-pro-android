package com.leedian.oviewremote.utils.exception;

import com.leedian.oviewremote.AppResource;
import com.leedian.oviewremote.R;

/**
 * DomainException
 *
 * @author Franco
 */
public class DomainException extends Exception {

    static public int EXCEPTION_ACTION_RESTART_APP = 1;

    private int exceptionActionCode;
    int exceptionMajor;
    int exceptionMinor;


    DomainException() {
        super();
    }

    DomainException(int major, int minor, final String message) {
        super(message);

        exceptionMajor = major;
        exceptionMinor = minor;
    }

    DomainException(final String message) {

        super(message);
    }

    DomainException(final String message, final Throwable cause) {

        super(message, cause);
    }

    DomainException(final Throwable cause) {

        super(cause);
    }

    public int getExceptionActionCode() {

        return exceptionActionCode;
    }

    public void setExceptionActionCode(int exceptionActionCode) {

        this.exceptionActionCode = exceptionActionCode;
    }


    static public Exception getDomainException(Exception e) {

        String cause = e.toString();
        if (cause.toUpperCase().contains("ENETUNREACH".toUpperCase()))
            return new NetworkConnectionException(AppResource.getString(R.string.no_available_network), NetworkConnectionException.SYS_NO_NETWORK, e.getCause());
        if (cause.toUpperCase().contains("Socket".toUpperCase()) && cause.toUpperCase().contains("Timeout".toUpperCase()))
            return new NetworkConnectionException(AppResource.getString(R.string.no_available_network), NetworkConnectionException.SYS_NO_NETWORK, e.getCause());
        if (cause.toUpperCase().contains("Failed".toUpperCase()) && cause.toUpperCase().contains("connect".toUpperCase()))
            return new NetworkConnectionException(AppResource.getString(R.string.no_available_network), NetworkConnectionException.SYS_NO_NETWORK, e.getCause());

        return e;
    }

    static public Exception buildHttpException(int major, int minor) {

        if (major == 1)
            return buildExceptionAuth(minor);
        if (major == 2)
            return buildExceptionAuth2(minor);
        return new Exception("unknown");
    }

    static public Exception buildFileException() {

        return new FileException(AppResource.getString(R.string.file_formation_invalid));
    }

    static Exception buildExceptionAuth(int minor) {

        if (minor == 1)
            return new AuthException(1,minor,AppResource.getString(R.string.sign_in_Illegal_Access_Token));

        if (minor == 2) {
            AuthException e = new AuthException(1,minor,AppResource.getString(R.string.sign_in_Illegal_Access_Token));
            e.setExceptionActionCode(DomainException.EXCEPTION_ACTION_RESTART_APP);
            return e;
        }

        if (minor == 3) {
            AuthException e = new AuthException(1,minor,AppResource.getString(R.string.sign_in_Access_Token_is_expired));
            e.setExceptionActionCode(DomainException.EXCEPTION_ACTION_RESTART_APP);
            return e;
        }

        if (minor == 5)
            return new AuthException(1,minor,AppResource.getString(R.string.sign_in_password_error));

        return new AuthException(1,minor,AppResource.getString(R.string.sign_in_password_error));
    }

    static Exception buildExceptionAuth2(int minor) {

        if (minor == 1)
            return new AuthException(1,minor,AppResource.getString(R.string.sign_in_brand_error));

        if (minor == 2)
            return new AuthException(2,minor,AppResource.getString(R.string.sign_in_user_error));

        return new AuthException(2,minor,AppResource.getString(R.string.sign_in_user_error));
    }

    static public boolean isExceptionAuth(Exception e) {

        return e instanceof AuthException;
    }




}
