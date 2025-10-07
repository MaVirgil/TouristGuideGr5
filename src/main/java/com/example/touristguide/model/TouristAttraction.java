package com.example.touristguide.model;

import java.util.ArrayList;
import java.util.List;

public class TouristAttraction {
    private int id;
    private String name;
    private String description;
    private String city;
    private List<String> selectedTags = new ArrayList<>();

    public TouristAttraction(int id, String name, String description, String city, List<String> tagList) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.city = city;
        this.selectedTags = tagList;
    }

    public TouristAttraction(){}

    public int getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getShortenedDescription() {

        int MAX_LENGTH = 28;

        if (description.length() > MAX_LENGTH) {
            return description.substring(0, MAX_LENGTH) + "...";
        } else {
            return description;
        }
    }

    public String getCity() {
        return city;
    }

    public List<String> getSelectedTags() {
        return selectedTags;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setSelectedTags(List<String> tagList) {
        this.selectedTags = tagList;
    }
}
