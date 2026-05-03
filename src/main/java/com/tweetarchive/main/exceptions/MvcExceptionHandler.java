package com.tweetarchive.main.exceptions;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.tweetarchive.main.model.enums.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ControllerAdvice
public class MvcExceptionHandler {
        private final MessageSource messageSource;

        @ExceptionHandler(ResourceNotFoundException.class)
        public String handleResourceNotFound(Model model, ResourceNotFoundException ex) {
                String message = resolveMessage(ex.getCode());
                model.addAttribute("error", message);
                return "error/404";
        }

        @ExceptionHandler({ NoHandlerFoundException.class, MethodArgumentTypeMismatchException.class })
        public String handleTypeMismatch(Model model) {
                String message = resolveMessage(ErrorCode.INVALID_URL);
                model.addAttribute("error", message);
                return "error/404";
        }

        @ExceptionHandler(Exception.class)
        public String generic(Model model) {
                model.addAttribute("error", "Error inesperado");
                return "error";
        }

        private String resolveMessage(ErrorCode code) {
                return messageSource.getMessage(
                                "error." + code.name(),
                                null,
                                LocaleContextHolder.getLocale());
        }
}