package org.example.konta_walutowe.library;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
