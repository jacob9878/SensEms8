package com.imoxion.sensems.server.exception;

public class ImSenderCloseException extends ImSenderException {
    public ImSenderCloseException(String errorCode) {
        super(errorCode);
    }

    public ImSenderCloseException(String errorCode, String[] args) {
        super(errorCode, args);
    }
}
