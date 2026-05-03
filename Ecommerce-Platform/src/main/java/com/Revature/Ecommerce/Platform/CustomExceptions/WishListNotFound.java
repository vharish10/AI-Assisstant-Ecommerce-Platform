package com.Revature.Ecommerce.Platform.CustomExceptions;

public class WishListNotFound extends RuntimeException{
    public WishListNotFound(String message){
        super(message);
    }
}
