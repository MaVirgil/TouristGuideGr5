package com.example.touristguide.service;

import com.example.touristguide.model.Tags;
import com.example.touristguide.model.TouristAttraction;
import com.example.touristguide.repository.AttractionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AttractionService {
    private final AttractionRepository repository;

    public AttractionService(AttractionRepository repository) {
        this.repository =repository;
    }

    public List<TouristAttraction> getAttractions(){
        return this.repository.getAttractions();
    }

    public List<String> getCities() {
        return this.repository.getCities();
    }

    public TouristAttraction getAttractionByName(String name){
        return this.repository.getAttractionByName(name);
    }

    public TouristAttraction addAttraction(TouristAttraction attraction) {
        return this.repository.addAttraction(attraction);
    }

    public void editAttraction(TouristAttraction attraction){

        String name = attraction.getName();
        String newDescription = attraction.getDescription();
        ArrayList<Tags> newTags = attraction.getSelectedTags();
        String newCity = attraction.getCity();

        this.repository.editAttraction(name, newDescription, newCity, newTags);
    }

    public void deleteAttraction(String name){
        this.repository.deleteAttraction(name);
    }
}
