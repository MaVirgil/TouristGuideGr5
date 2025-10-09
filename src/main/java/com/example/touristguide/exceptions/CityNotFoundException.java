package com.example.touristguide.exceptions;

import com.example.touristguide.exceptions.service.AttractionServiceException;

public class CityNotFoundException extends AttractionServiceException {
    public CityNotFoundException(String city) {
        super("City '" + city + "' not found");
    }
}
