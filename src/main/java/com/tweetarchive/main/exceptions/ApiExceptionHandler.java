package com.tweetarchive.main.exceptions;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CollectionNotFoundException.class)
    public ResponseEntity<?> handleCollectionNotFound() {
        return ResponseEntity.status(404).body(
                Map.of(
                        "status", "error",
                        "message", "La colección no existe"
                )
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound() {
        return ResponseEntity.status(404).body(
                Map.of(
                        "status", "error",
                        "message", "El usuario no existe"
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return ResponseEntity.status(500).body(
                Map.of(
                        "status", "error",
                        "message", "Error inesperado"
                )
        );
    }
}