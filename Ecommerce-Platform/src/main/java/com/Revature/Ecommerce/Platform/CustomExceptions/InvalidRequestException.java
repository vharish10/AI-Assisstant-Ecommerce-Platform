package com.Revature.Ecommerce.Platform.CustomExceptions;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message){
        super(message);
    }
}
