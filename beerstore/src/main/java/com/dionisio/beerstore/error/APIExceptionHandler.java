package com.dionisio.beerstore.error;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.dionisio.beerstore.error.ErrorResponse.APIError;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class APIExceptionHandler {

    private static final String NO_MESSAGE_AVAILABLE = "No message available";
    private static final Logger LOG = LoggerFactory.getLogger(APIExceptionHandler.class);

    private final MessageSource apiErrorMessageSource;

    public APIExceptionHandler(MessageSource apiErrorMessageSource) {
        this.apiErrorMessageSource = apiErrorMessageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handlerNotValidException(MethodArgumentNotValidException exception,
            Locale locale) {

        Stream<ObjectError> errors = exception.getBindingResult().getAllErrors().stream();

        List<APIError> apiErrors = errors
                .map(ObjectError::getDefaultMessage)
                .map(code -> toAPIError(code, locale))
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST, apiErrors);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorResponse> handlerInvalidFormatException(InvalidFormatException exception,
            Locale locale) {
        final String errorCode = "generic-1";
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        final ErrorResponse errorResponse = ErrorResponse.of(status, toAPIError(errorCode, locale, exception.getValue()));

        return ResponseEntity.badRequest().body(errorResponse);
    }

    private APIError toAPIError(String code, Locale locale, Object... args) {
        String message;
        try {
            message = apiErrorMessageSource.getMessage(code, args, locale);
        } catch (NoSuchMessageException e) {
            LOG.error("Could not find any message for {} code under {} locale", code, locale);
            message = NO_MESSAGE_AVAILABLE;
        }

        return new APIError(code, message);
    }

}