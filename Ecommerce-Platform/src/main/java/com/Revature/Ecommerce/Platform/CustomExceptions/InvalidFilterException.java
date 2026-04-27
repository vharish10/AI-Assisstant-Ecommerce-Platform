package com.Revature.Ecommerce.Platform.CustomExceptions;

public class InvalidFilterException extends RuntimeException {
    public InvalidFilterException(String message) {
        super(message);
    }
}