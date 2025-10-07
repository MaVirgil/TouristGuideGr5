package com.example.touristguide.repository;

import com.example.touristguide.model.Tag;
import com.example.touristguide.model.TouristAttraction;
import com.example.touristguide.service.AttractionService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
public class AttractionRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<TouristAttraction> attractionMapper = (rs, rowNum) -> {
        TouristAttraction attraction = new TouristAttraction();

        attraction.setId(rs.getInt("id"));
        attraction.setName(rs.getString("name"));
        attraction.setDescription(rs.getString("description"));
        attraction.setCity(rs.getString("city_name"));
        return attraction;
    };

    private final RowMapper<Tag> tagMapper = (rs, rowNum) -> new Tag(
            rs.getInt("id"),
            rs.getString("name")
    );

    public AttractionRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

//    public List<TouristAttraction> getAttractions() {

//        String attractionsQuery = """
//                SELECT A.id as id, A.name AS name, A.description AS description,
//                C.name AS city_name FROM Attraction A
//                INNER JOIN City C ON A.city_id = C.id
//                """;
//
//        //get list of attractions (without tagList)
//        List<TouristAttraction> attractions = jdbcTemplate.query(attractionsQuery, attractionMapper);
//
//
//        //Query to retrieve all tags for specific attraction
//        String tagListByIdQuery = """
//                SELECT Tag.name AS tag_name
//                FROM Tags_attraction_junction TAJ
//                INNER JOIN Tag ON Tag.id = TAJ.tag_id
//                WHERE TAJ.attraction_id = ?;
//                """;
//
//        //map tag names (as List<String>) to each attracion
//        for (TouristAttraction attraction : attractions) {
//            List<String> tagList = jdbcTemplate.queryForList(tagListByIdQuery, String.class, attraction.getId());
//            attraction.setSelectedTags(tagList);
//        }
//
//        return attractions;
//    }

    public List<TouristAttraction> getAttractions() {

        String query = """
        SELECT 
            A.id AS id,
            A.name AS name,
            A.description AS description,
            C.name AS city_name,
            GROUP_CONCAT(T.name SEPARATOR ',') AS tags
        FROM Attraction A
        INNER JOIN City C ON A.city_id = C.id
        LEFT JOIN Tags_Attraction_Junction TAJ ON A.id = TAJ.attraction_id
        LEFT JOIN Tag T ON TAJ.tag_id = T.id
        GROUP BY A.id, A.name, A.description, C.name
        ORDER BY A.name;
        """;

        return jdbcTemplate.query(query, (rs, rowNum) -> {
            TouristAttraction attraction = new TouristAttraction();
            attraction.setId(rs.getInt("id"));
            attraction.setName(rs.getString("name"));
            attraction.setDescription(rs.getString("description"));
            attraction.setCity(rs.getString("city_name"));

            String tagsString = rs.getString("tags");
            if (tagsString != null) {
                List<String> tags = Arrays.asList(tagsString.split(","));
                attraction.setSelectedTags(tags);
            } else {
                attraction.setSelectedTags(Collections.emptyList());
            }
            return attraction;
        });
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

        String name = attraction.getName().trim();
        attraction.setName(name);

        if (name.isBlank()) {
            throw new IllegalArgumentException("Attraction name must not be empty");
        }

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM Attraction WHERE name = ?", Integer.class, name);

        if (count != null && count > 0) {
            return null;
        }

        int cityId = getCityByName(attraction.getCity());
        if (cityId == -1) {
            throw new RuntimeException("City not found: " + attraction.getCity());
        }

        String insertSql = "INSERT INTO Attraction (name, description, city_id) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, attraction.getName());
                ps.setString(2, attraction.getDescription());
                ps.setInt(3, cityId);
                return ps;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            return null;
        }

        Number generatedKey = keyHolder.getKey();
        if (generatedKey == null) {
            throw new RuntimeException("Failed to obtain generated key for Attraction");
        }
        int newAttractionId = generatedKey.intValue();
        attraction.setId(newAttractionId);

        Map<String, Integer> tagMap = getTags();
        if (attraction.getSelectedTags() != null) {
            for (String tagName : attraction.getSelectedTags()) {
                Integer tagId = tagMap.get(tagName);
                if (tagId == null) {
                    jdbcTemplate.update("INSERT INTO Tag (name) VALUES (?)", tagName);
                    tagId = jdbcTemplate.queryForObject("SELECT id FROM Tag WHERE name = ?", Integer.class, tagName);
                    tagMap.put(tagName, tagId);
                }
                addAttractionTagsByID(newAttractionId, tagId);
            }
        }
        return attraction;
    }

    public TouristAttraction editAttraction(TouristAttraction attractionToEdit) {

        int newCityId = this.getCityByName(attractionToEdit.getCity());
        if (newCityId == -1) {
            // Handle error: City not found (though you said it's successful)
            System.err.println("Error: City '" + attractionToEdit.getCity() + "' not found during update.");
            throw new RuntimeException("Cannot update attraction: City not found.");
        }

        String updateQuery = """
            UPDATE Attraction
            SET 
                description = ?,
                city_id = ?
            WHERE id = ?
            """;

        Object[] args = {
                attractionToEdit.getDescription(),
                newCityId,
                attractionToEdit.getId()
        };


        int rowsAffected = jdbcTemplate.update(updateQuery, args);

        // Diagnostic step: Confirm the row was updated
        if (rowsAffected == 0) {
            System.err.println("Error: No row found to update for ID: " + attractionToEdit.getId());
            throw new RuntimeException("Attraction update failed: ID not found.");
        }

        List<String> tags = attractionToEdit.getSelectedTags();

        this.deleteTagsByAttractionID(attractionToEdit.getId());

        //repopulate junction table with new tags
        Map<String, Integer> tagMap = this.getTags();

        for (String tag : attractionToEdit.getSelectedTags()) {
            this.addAttractionTagsByID(attractionToEdit.getId(), tagMap.get(tag));
        }

        return this.getAttractionById(attractionToEdit.getId());

    }

    public Map<String, Integer> getTags() {

        String query = """
                SELECT name, id
                FROM Tag
                """;

        List<Map<String, Object>> rows = jdbcTemplate.queryForList((query));

        Map<String, Integer> resultset = new HashMap<>();
        for(Map<String, Object> row : rows) {
            resultset.put((String) row.get("name"), (Integer) row.get("id"));
        }

        return resultset;

    }

    public int deleteTagsByAttractionID(int attractionId){
        String deleteSql = "DELETE FROM Tags_Attraction_Junction WHERE attraction_id = ?";

        Object[] args = {
                attractionId
        };

        return jdbcTemplate.update(deleteSql, args);
    }

    public int addAttractionTagsByID(int attractionId, int tagId){
        String insertSql = "INSERT INTO Tags_Attraction_Junction (attraction_id, tag_id) values (?, ?)";

        Object[] args = {
                attractionId,
                tagId
        };

        return jdbcTemplate.update(insertSql, args);
    }

    public int getCityByName(String cityName) {
        String selectSql = "SELECT id FROM City WHERE name = ?";

        try {
            System.out.println("Looking up city: " + cityName);
            int cityId = jdbcTemplate.queryForObject(selectSql, Integer.class, cityName);
            System.out.println("Found city:" + cityId);

            return cityId;

        } catch(EmptyResultDataAccessException e) {
            System.out.println("City not found:" + cityName);

            return -1;
        }
    }

    public int deleteAttraction(int id) {

        String deleteFromJunctionTable = "DELETE FROM Tags_Attraction_Junction WHERE attraction_id = ?";
        jdbcTemplate.update(deleteFromJunctionTable, id);

        String deleteFromAttractionTable = "DELETE FROM Attraction WHERE id = ?";
        return jdbcTemplate.update(deleteFromAttractionTable, id);
    }

    public List<String> getAllTagNames(){
        String sql = "SELECT name FROM Tag ORDER BY name";
        return jdbcTemplate.queryForList(sql, String.class);
    }

}


