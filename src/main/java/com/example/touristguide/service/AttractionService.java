package com.example.touristguide.service;

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
        return this.repository.getAttractionById(id);
    }

    public TouristAttraction addAttraction(TouristAttraction attraction) {
        return this.repository.addAttraction(attraction);
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
