package com.shikhar.VisionCraft.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.LocalDateTime;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleImageNotFound(
            ImageNotFoundException exception
    ) {

        ErrorResponse error =
                new ErrorResponse(
                        exception.getMessage(),
                        LocalDateTime.now()
                );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }


    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(
            IOException exception
    ) {

        ErrorResponse error =
                new ErrorResponse(
                        "Unable to process image",
                        LocalDateTime.now()
                );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
