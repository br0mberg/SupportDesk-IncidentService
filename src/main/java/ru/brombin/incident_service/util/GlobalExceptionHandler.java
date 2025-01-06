package ru.brombin.incident_service.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.brombin.incident_service.builder.LogInfoBuilder;
import ru.brombin.incident_service.util.exceptions.ImageProcessingException;
import ru.brombin.incident_service.util.exceptions.NotFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(NotFoundException e, WebRequest request) {
        log.info("Entity not found: {}", e.getMessage(), e, request);
        return new ErrorResponse("Resource not found", System.currentTimeMillis());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException e, WebRequest request) {
        String validationErrors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validation failed: {}", validationErrors, e, request);
        return new ErrorResponse("Validation error: " + validationErrors, System.currentTimeMillis());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ImageProcessingException.class)
    public ErrorResponse handleImageProcessingException(ImageProcessingException e, WebRequest request) {
        log.error("Image processing error: {}", e.getMessage(), e, request);
        return new ErrorResponse("Image processing error", System.currentTimeMillis());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGlobalException(Exception e, WebRequest request) {
        log.error("Unexpected error: {}", e.getMessage(), e, request);
        return new ErrorResponse("An unexpected error occurred", System.currentTimeMillis());
    }
}
