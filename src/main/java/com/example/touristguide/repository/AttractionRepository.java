package com.example.touristguide.repository;

import com.example.touristguide.model.Tag;
import com.example.touristguide.model.TouristAttraction;
import com.example.touristguide.service.AttractionService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AttractionRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<TouristAttraction> attractionMapper = (rs, rowNum) -> {
        TouristAttraction attraction = new TouristAttraction();

        attraction.setId(rs.getInt("id"));
        attraction.setName(rs.getString("name"));
        attraction.setDescription(rs.getString("desc"));
        attraction.setCity(rs.getString("city_name"));
        return attraction;
    };

    private final RowMapper<Tag> tagMapper = (rs, rowNum) -> new Tag(
            rs.getInt("id"),
            rs.getString("name")
    );

    public AttractionRepository(JdbcTemplate jdbcTemplate, AttractionService attractionService, DataSourceTransactionManager dataSourceTransactionManager){
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TouristAttraction> getAttractions() {

        String attractionsQuery = """
                SELECT A.id as id, A.name AS name, A.description AS description,
                C.name AS city_name FROM Attraction A
                INNER JOIN City C ON A.city_id = C.id
                """;

        //get list of attractions (without tagList)
        List<TouristAttraction> attractions = jdbcTemplate.query(attractionsQuery, attractionMapper);


        //Query to retrieve all tags for specific attraction
        String tagListByIdQuery = """
                SELECT Tag.name AS tag_name
                FROM Tags_attraction_junction TAJ
                INNER JOIN Tag ON Tag.id = TAJ.tag_id
                WHERE TAJ.attraction_id = ?;
                """;

        //map tag names (as List<String>) to each attracion
        for (TouristAttraction attraction : attractions) {
            List<String> tagList = jdbcTemplate.queryForList(tagListByIdQuery, String.class, attraction.getId());
            attraction.setSelectedTags(tagList);
        }

        return attractions;
    }

    public List<String> getCities() {

        String query = """
                SELECT name
                FROM CITY
                ORDER BY name;
                """;

        return jdbcTemplate.queryForList(query, String.class);
    }

    public TouristAttraction getAttractionById(int id) {

        for (TouristAttraction attraction : this.getAttractions()) {
            if (attraction.getId() == id) return attraction;
        }

        return null;
    }

    public TouristAttraction addAttraction(TouristAttraction attraction) {

        //remove whitespaces from start and end of name
        attraction.setName(attraction.getName().trim());

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


