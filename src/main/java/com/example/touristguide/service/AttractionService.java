package com.example.touristguide.service;

import com.example.touristguide.exceptions.AttractionExistException;
import com.example.touristguide.exceptions.AttractionNotFoundException;
import com.example.touristguide.exceptions.AttractionValidationException;
import com.example.touristguide.exceptions.CityNotFoundException;
import com.example.touristguide.model.TouristAttraction;
import com.example.touristguide.repository.AttractionRepository;
import org.springframework.stereotype.Service;
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

        TouristAttraction attraction = this.repository.getAttractionById(id);

        if(attraction == null){
            throw new AttractionNotFoundException(id);
        }

        return attraction;
    }

    public TouristAttraction addAttraction(TouristAttraction attraction) {

        String name = attraction.getName();
        attraction.setName(name);

        //Handling exception for blank name
        if (name.isBlank()) {
            throw new AttractionValidationException("name", "must not be empty");
        }

        if(repository.existsByName(name)){
            throw new AttractionExistException(name);
        }

        int cityId = repository.getCityByName(attraction.getCity());

        if (cityId == -1) {
            throw new CityNotFoundException(attraction.getCity());
        }

        int newAttractionId = repository.addAttraction(attraction, cityId);
        attraction.setId(newAttractionId);

        return this.repository.addAttraction(attraction, cityId);
    }


    public TouristAttraction editAttraction(TouristAttraction attraction){
        return repository.editAttraction(attraction);
    }

    public List<String> getAllTagNames(){
        return this.repository.getAllTagNames();
    }
    public void deleteAttraction(int id){
        this.repository.deleteAttraction(id);
    }
}
