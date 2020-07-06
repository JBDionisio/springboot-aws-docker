package com.dionisio.beerstore.error;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

@JsonAutoDetect(fieldVisibility = ANY)
public class ErrorResponse {

    @NonNull
    private final int statusCode;
    @NonNull
    private final List<APIError> errors; 

    private ErrorResponse(int statusCode, List<APIError> errors) {
        this.statusCode = statusCode;
        this.errors = errors;
    }

    static ErrorResponse of(HttpStatus status, List<APIError> errors) {
        return new ErrorResponse(status.value(), errors);
    }

    static ErrorResponse of(HttpStatus status, APIError error) {
        return of(status, Collections.singletonList(error));
    }

    @JsonAutoDetect(fieldVisibility = ANY)
    static class APIError {
        @NonNull
        private final String code;
        @NonNull
        private final String message;
        
        public APIError(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }

}