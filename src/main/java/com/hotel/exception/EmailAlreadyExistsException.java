package com.hotel.util;

public class EmailAlreadyExistsException extends BusinessException{
    public EmailAlreadyExistsException(String message){
        super(message);
    }
}