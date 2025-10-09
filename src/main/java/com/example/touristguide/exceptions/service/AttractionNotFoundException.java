package com.example.touristguide.exceptions.service;

public class AttractionNotFoundException extends RuntimeException{
    public AttractionNotFoundException(int id){
        super("Attraction not found with ID: " + id);
    }
}
