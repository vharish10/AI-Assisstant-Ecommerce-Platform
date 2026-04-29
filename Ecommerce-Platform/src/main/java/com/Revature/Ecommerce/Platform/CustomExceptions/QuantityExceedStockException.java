package com.Revature.Ecommerce.Platform.CustomExceptions;

public class QuantityExceedStockException extends RuntimeException{
    public QuantityExceedStockException(String message){
        super(message);
    }
}
