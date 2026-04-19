package com.tweetarchive.main.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CollectionNotFoundException.class)
    public Object handleCollectionNotFound(
            HttpServletRequest request,
            Model model) {

        if (isApiRequest(request)) {
            return ResponseEntity.status(404).body(
                    Map.of(
                            "status", "error",
                            "message", "La colección no existe"
                    )
            );
        }

        model.addAttribute("error", "La colección no existe");
        return "error/404";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public Object handleUserNotFound(
            HttpServletRequest request,
            Model model) {

        if (isApiRequest(request)) {
            return ResponseEntity.status(404).body(
                    Map.of(
                            "status", "error",
                            "message", "El usuario no existe"
                    )
            );
        }

        model.addAttribute("error", "El usuario no existe");
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneric(
            HttpServletRequest request,
            Model model) {

        if (isApiRequest(request)) {
            return ResponseEntity.status(500).body(
                    Map.of(
                            "status", "error",
                            "message", "Error inesperado"
                    )
            );
        }

        model.addAttribute("error", "Error inesperado");
        return "error";
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains("application/json");
    }
}