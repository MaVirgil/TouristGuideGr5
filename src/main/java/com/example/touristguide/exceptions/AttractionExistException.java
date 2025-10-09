package com.example.touristguide.exceptions;

import com.example.touristguide.exceptions.service.AttractionServiceException;

public class AttractionExistException extends AttractionServiceException {
    public AttractionExistException(String message) {
        super(message);
    }
}
