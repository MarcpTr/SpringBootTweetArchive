package com.tweetarchive.main.exceptions;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tweetarchive.main.model.DTO.RegisterRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CollectionNotFoundException.class)
    public String handleCollectionNotFound(Model model) {
        model.addAttribute("error", "La colección no existe");
        return "error/404";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(Model model) {
        model.addAttribute("error", "El usuario no existe");
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneric(Model model) {
        model.addAttribute("error", "Error inesperado");
        return "error";
    }
}