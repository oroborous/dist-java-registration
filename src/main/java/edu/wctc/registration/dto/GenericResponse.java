package edu.wctc.registration.dto;

import lombok.Data;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class GenericResponse {
    private String message;
    private String error;

    public GenericResponse(String message) {
        this.message = message;
    }

    public GenericResponse(String message, String error) {
        this.message = message;
        this.error = error;
    }

    public GenericResponse(List<ObjectError> allErrors, String error) {
        this.error = error;
        String temp = allErrors.stream().map(e -> {
            // Build JSON object
            if (e instanceof FieldError) {
                return String.format("{\"field\":\"%s\",\"defaultMessage\":\"%s\"}",
                        ((FieldError) e).getField(),
                        e.getDefaultMessage());
            } else {
                return String.format("{\"object\":\"%s\",\"defaultMessage\":\"%s\"}",
                        e.getObjectName(),
                        e.getDefaultMessage());
            }
        }).collect(Collectors.joining(","));
        this.message = "[" + temp + "]";
    }
}
