package com.imoxion.sensems.server.exception;

public class SensemsException extends Exception {
    public SensemsException(){}

    public SensemsException(Throwable t){
        super(t);
    }

    public SensemsException(String message){
        super(message);
    }
    public SensemsException(String message, Throwable t){
        super(message,t);
    }
}
