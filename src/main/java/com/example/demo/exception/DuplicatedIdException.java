package com.example.demo.exception;

public class DuplicatedIdException extends RuntimeException{
    public DuplicatedIdException(String message) {
        super(message);
    }
}
