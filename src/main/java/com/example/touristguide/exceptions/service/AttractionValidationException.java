package com.example.touristguide.exceptions.service;

public class AttractionValidationException extends RuntimeException {
    public AttractionValidationException(String field, String message) {
        super(field + ": " + message);
    }
}
