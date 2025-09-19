package com.example.touristguide.repository;

import com.example.touristguide.model.Tags;
import com.example.touristguide.model.TouristAttraction;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AttractionRepository {

    private final List<TouristAttraction> attractions = new ArrayList<>();
    private final List<String> cities = new ArrayList<>();


    public AttractionRepository(){
        populateRepository();
    }

    private void populateRepository() {
        attractions.add(new TouristAttraction("Tivoli", "Copenhagen’s largest amusement park", "Copenhagen", new ArrayList<>(Arrays.asList(Tags.CHILDFRIENDLY, Tags.CONCERT, Tags.RESTAURANT, Tags.ENTERTAINMENT))));
        attractions.add(new TouristAttraction("Louisiana", "Famous art museum north of Copenhagen", "Humlebæk", new ArrayList<>(Arrays.asList(Tags.ART, Tags.NATURE))));
        attractions.add(new TouristAttraction("ARoS", "Aarhus Art Museum", "Aarhus", new ArrayList<>(Arrays.asList(Tags.ART, Tags.RESTAURANT, Tags.MUSEUM))));
        attractions.add(new TouristAttraction("Odense Zoo", "Animals form all over the world", "Odense", new ArrayList<>(Arrays.asList(Tags.ENTERTAINMENT, Tags.CHILDFRIENDLY, Tags.NATURE))));
        attractions.add(new TouristAttraction("Den blå planet", "Aquarium in Copenhagen", "Copenhagen", new ArrayList<>(Arrays.asList(Tags.ENTERTAINMENT, Tags.CHILDFRIENDLY, Tags.NATURE))));
        attractions.add(new TouristAttraction("Legoland", "LEGO amusement park", "Billund", new ArrayList<>(Arrays.asList(Tags.NATURE, Tags.CHILDFRIENDLY, Tags.ENTERTAINMENT))));

        cities.add("Copenhagen");
        cities.add("Aabenraa");
        cities.add("Aalborg");
        cities.add("Aarhus");
        cities.add("Birkerød");
        cities.add("Esbjerg");
        cities.add("Fredericia");
        cities.add("Frederiksberg");
        cities.add("Helsingør");
        cities.add("Herlev");
        cities.add("Herning");
        cities.add("Hillerød");
        cities.add("Holbæk");
        cities.add("Holstebro");
        cities.add("Horsens");
        cities.add("Kolding");
        cities.add("Køge");
        cities.add("Næstved");
        cities.add("Odense");
        cities.add("Randers");
        cities.add("Ringsted");
        cities.add("Roskilde");
        cities.add("Silkeborg");
        cities.add("Slagelse");
        cities.add("Sønderborg");
        cities.add("Svendborg");
        cities.add("Vejle");
        cities.add("Viborg");
        Collections.sort(cities);
    }

    public List<TouristAttraction> getAttractions() {
        return attractions;
    }

    public List<String> getCities() {
        return cities;
    }

    public TouristAttraction getAttractionByName(String name) {

        for (TouristAttraction attraction : attractions) {
            if (attraction.getName().equalsIgnoreCase(name)) return attraction;
        }

        return null;
    }

    public TouristAttraction addAttraction(TouristAttraction attraction) {

        //check if attraction of same name already exists in list
        if (this.getAttractionByName(attraction.getName()) != null) {
            return null;
        }

        this.attractions.add(attraction);

        return attraction;
    }

    public TouristAttraction editAttraction(String attractionName, String newDescription, String newCity, ArrayList<Tags> newTagList) {
        TouristAttraction attractionToEdit = getAttractionByName(attractionName);

        if (attractionToEdit != null) {
            attractionToEdit.setDescription(newDescription);
            attractionToEdit.setCity(newCity);
            attractionToEdit.setSelectedTags(newTagList);
        }

        return attractionToEdit;
    }

    public TouristAttraction deleteAttraction(String attractionName) {
        TouristAttraction attractionToDelete = getAttractionByName(attractionName);

        attractions.remove(attractionToDelete);

        return attractionToDelete;
    }

}


