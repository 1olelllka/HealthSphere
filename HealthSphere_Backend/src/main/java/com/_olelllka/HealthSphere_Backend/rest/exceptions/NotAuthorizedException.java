package com._olelllka.HealthSphere_Backend.rest.exceptions;

public class NotAuthorizedException extends RuntimeException{

    public NotAuthorizedException(String message) {
        super(message);
    }
}
