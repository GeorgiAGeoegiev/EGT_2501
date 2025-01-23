package com.example.demo.exception;

public class EmptyRecordException extends RuntimeException{
    public EmptyRecordException(String message) {
        super(message);
    }
}
