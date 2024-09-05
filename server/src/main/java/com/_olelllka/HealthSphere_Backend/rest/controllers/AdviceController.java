package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.dto.ErrorMessage;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotAuthorizedException;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.rest.exceptions.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AdviceController {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessage> notFoundException(NotFoundException ex) {
        return new ResponseEntity<>(ErrorMessage.builder().message(ex.getMessage()).build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorMessage> validationException(ValidationException ex) {
        return new ResponseEntity<>(ErrorMessage.builder().message(ex.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<ErrorMessage> notAuthorizedException(NotAuthorizedException ex) {
        return new ResponseEntity<>(ErrorMessage.builder().message(ex.getMessage()).build(), HttpStatus.UNAUTHORIZED);
    }

}
