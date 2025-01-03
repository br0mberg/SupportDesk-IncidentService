package ru.brombin.incident_service.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.brombin.incident_service.util.exceptions.ImageProcessingException;
import ru.brombin.incident_service.util.exceptions.NotFoundException;
import ru.brombin.incident_service.util.messages.GeneralLogMessages;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(NotFoundException e, WebRequest request) {
        log.warn(GeneralLogMessages.NOT_FOUND.getFormatted(e.getMessage()), request.getDescription(false));
        return new ErrorResponse(e.getMessage(), System.currentTimeMillis());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException e, WebRequest request) {
        log.warn(GeneralLogMessages.VALIDATION_FAILED.getFormatted(e.getMessage()));
        StringBuilder errors = new StringBuilder("Validation errors: ");
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; "));

        return new ErrorResponse(errors.toString(), System.currentTimeMillis());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ImageProcessingException.class)
    public ErrorResponse handleImageProcessingException(ImageProcessingException e, WebRequest request) {
        log.error(GeneralLogMessages.IMAGE_PROCESSING_ERROR.getFormatted(e.getMessage()), request.getDescription(false), e);
        return new ErrorResponse(GeneralLogMessages.IMAGE_PROCESSING_ERROR.getFormatted() + e.getMessage(), System.currentTimeMillis());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGlobalException(Exception e, WebRequest request) {
        log.error(GeneralLogMessages.UNEXPECTED_ERROR.getFormatted(e.getMessage()), request.getDescription(false), e);
        return new ErrorResponse(GeneralLogMessages.UNEXPECTED_ERROR.getFormatted(), System.currentTimeMillis());
    }
}
