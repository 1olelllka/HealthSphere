package com._olelllka.HealthSphere_Backend.rest.exceptions;

public class DuplicateException extends RuntimeException{
    public DuplicateException(String message) {
        super(message);
    }
}