package com.hotel.exception;


public class InvalidCredentialsException extends BusinessException{
    public InvalidCredentialsException(String message){
        super(message);
    }
}