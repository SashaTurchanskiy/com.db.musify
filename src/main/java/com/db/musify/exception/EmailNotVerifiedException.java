package com.db.musify.exception;

public class EmailNotVerifiedException extends RuntimeException{
    public EmailNotVerifiedException(String message){
        super(message);
    }
}
