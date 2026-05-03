package com.tweetarchive.main.exceptions;

import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.tweetarchive.main.model.DTO.ApiError;
import com.tweetarchive.main.model.DTO.ApiResponse;
import com.tweetarchive.main.model.enums.ErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestControllerAdvice(annotations = RestController.class)
public class ApiExceptionHandler {
        private final MessageSource messageSource;

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ApiResponse<Void>> handleCollectionNotFound(AuthenticationException ex) {
                String message = resolveMessage(ex.getCode());
                ApiError<Object> error = new ApiError<>(
                                ex.getCode().name(),
                                message,
                                ex.getDetails());
                return ResponseEntity.status(401)
                                .body(ApiResponse.fail(error));
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleResourceNotFound(ResourceNotFoundException ex) {
                String message = resolveMessage(ex.getCode());
                ApiError<Object> error = new ApiError<>(
                                ex.getCode().name(),
                                message,
                                ex.getDetails());
                return ResponseEntity.status(404)
                                .body(ApiResponse.fail(error));
        }

        @ExceptionHandler(AlreadyLikedException.class)
        public ResponseEntity<ApiResponse<Void>> alreadyLikedException(AlreadyLikedException ex) {
                String message = resolveMessage(ex.getCode());
                ApiError<Object> error = new ApiError<>(
                                ex.getCode().name(),
                                message,
                                ex.getDetails());
                return ResponseEntity.status(409)
                                .body(ApiResponse.fail(error));
        }

        @ExceptionHandler(NotLikedException.class)
        public ResponseEntity<ApiResponse<Void>> notLikedException(NotLikedException ex) {
                String message = resolveMessage(ex.getCode());
                ApiError<Object> error = new ApiError<>(
                                ex.getCode().name(),
                                message,
                                ex.getDetails());
                return ResponseEntity.status(409)
                                .body(ApiResponse.fail(error));
        }

        @ExceptionHandler(ForbiddenOperationException.class)
        public ResponseEntity<ApiResponse<Void>> forbidden(ForbiddenOperationException ex) {
                return ResponseEntity.status(403)
                                .body(ApiResponse.fail("FORBIDDEN", ex.getMessage(), null));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Void>> generic() {
                return ResponseEntity.status(500)
                                .body(ApiResponse.fail("INTERNAL_ERROR", "Error inesperado", null));
        }

        private String resolveMessage(ErrorCode code) {
                return messageSource.getMessage(
                                "error." + code.name(),
                                null,
                                LocaleContextHolder.getLocale());
        }
}