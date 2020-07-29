package com.example.demo.bean;

public class FileException extends RuntimeException {
    private static final long serialVersionUID = 4651514738390935516L;

    public FileException(String message) {
        super(message);
    }

    public FileException(String message, Throwable cause) {
        super(message, cause);
    }
}