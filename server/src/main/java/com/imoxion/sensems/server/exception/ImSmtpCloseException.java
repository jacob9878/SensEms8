package com.imoxion.sensems.server.exception;

public class ImSmtpCloseException extends ImSmtpException {
    public ImSmtpCloseException(String errorCode) {
        super(errorCode);
    }

    public ImSmtpCloseException(String errorCode, String[] args) {
        super(errorCode, args);
    }
}
