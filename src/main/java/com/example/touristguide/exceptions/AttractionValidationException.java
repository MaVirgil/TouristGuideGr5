package com.example.touristguide.exceptions;

import com.example.touristguide.exceptions.service.AttractionServiceException;

public class AttractionValidationException extends AttractionServiceException {
    public AttractionValidationException(String field, String message) {
        super(field + ": " + message);
    }
}
