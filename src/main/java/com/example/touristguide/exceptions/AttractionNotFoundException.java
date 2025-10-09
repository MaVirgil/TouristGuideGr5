package com.example.touristguide.exceptions;

import com.example.touristguide.exceptions.service.AttractionServiceException;

public class AttractionNotFoundException extends AttractionServiceException {
    public AttractionNotFoundException(int id){
        super("Attraction not found with ID: " + id);
    }
}
