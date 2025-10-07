package com.example.touristguide.service;

import com.example.touristguide.model.Tag;
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

    public TouristAttraction getAttractionById(int id){
        return this.repository.getAttractionById(id);
    }

    public TouristAttraction addAttraction(TouristAttraction attraction) {
        return this.repository.addAttraction(attraction);
    }


    public TouristAttraction editAttraction(TouristAttraction attraction){
        /*Integer getCityId = repository.getCityByName(attraction.getCity());

        if(getCityId == null) {
            System.err.println("Error: City " + attraction.getCity() + " not found");

            return null;
        }

        TouristAttraction newAttraction = new TouristAttraction();

        newAttraction.setId(attraction.getId());
        newAttraction.setName(attraction.getName());
        newAttraction.setDescription(attraction.getDescription());
        ArrayList<Tag> newTags = attraction.getSelectedTags();

        int rowsUpdated = repository.editAttraction(newAttraction);

        repository.deleteTagsByAttractionID(attraction.getId());
        for (Tag tag: newTags) {
            repository.addAttractionTagsByID(attraction.getId(), tag.getId());
        }

        newAttraction.setSelectedTags(newTags);

        int id = attraction.getId();
        String name = attraction.getName();
        String newDescription = attraction.getDescription();
        ArrayList<Tag> newTags = attraction.getSelectedTags();
        String newCity = attraction.getCity();*/

        return repository.editAttraction(attraction);
    }

    public void deleteAttraction(int id){
        this.repository.deleteAttraction(id);
    }
}
