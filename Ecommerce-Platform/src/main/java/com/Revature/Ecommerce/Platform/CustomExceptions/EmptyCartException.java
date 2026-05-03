package com.Revature.Ecommerce.Platform.CustomExceptions;

public class EmptyCartException extends RuntimeException{
    public EmptyCartException(String message){
        super(message);
    }
}
