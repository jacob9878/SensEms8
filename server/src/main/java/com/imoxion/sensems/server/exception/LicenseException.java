package com.imoxion.sensems.server.exception;

public class LicenseException extends Exception {

    private int errorCode;
    private String errorMessage;

    public final static int NOT_FOUND = 100;

    public final static int EXPIRE = 101;

    public final static int NOT_AVAILABLE = 102;

    public final static int GENERAL_ERROR = 103;

    public LicenseException(int errorCode){
        this.errorCode = errorCode;
    }

    public LicenseException(int errorCode, String errMsg){
        this.errorCode = errorCode;
        this.errorMessage = errMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
}