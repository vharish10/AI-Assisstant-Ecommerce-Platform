package com.Revature.Ecommerce.Platform.CustomExceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}