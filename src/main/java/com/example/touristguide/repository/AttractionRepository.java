package com.example.touristguide.repository;

import com.example.touristguide.model.TouristAttraction;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.*;

@Repository
public class AttractionRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<TouristAttraction> attractionRowMapper = (rs, rowNum) -> {
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
    };

    public AttractionRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

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
        GROUP BY A.id
        ORDER BY A.name;
        """;

        return jdbcTemplate.query(query, attractionRowMapper);
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
        WHERE A.id = ?
        GROUP BY A.id
        ORDER BY A.name;
        """;

        //Handling of AttractionNotFound exception so it return null for the service
        try {
            return jdbcTemplate.queryForObject(query, attractionRowMapper, id);
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public int addAttraction(TouristAttraction attraction, int cityId) {
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
            throw e;
        }

        Number generatedKey = keyHolder.getKey();
        if (generatedKey == null) {
            throw new RuntimeException("Failed to obtain generated key for Attraction");
        }

        return generatedKey.intValue();
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

        //Confirm the row was updated
        if (rowsAffected == 0) {
            System.err.println("Error: No row found to update for ID: " + attractionToEdit.getId());
            throw new RuntimeException("Attraction update failed: ID not found.");
        }

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
        String deleteFromAttractionTable = "DELETE FROM Attraction WHERE id = ?";
        String deleteFromJunctionTable = "DELETE FROM Tags_Attraction_Junction WHERE attraction_id = ?";

        System.out.println("Attempting  to delete from junction table");
        int rowsAffectedJunction = jdbcTemplate.update(deleteFromJunctionTable, id);

        System.out.println("Attempting to delete from attraction table");
        int rowsAffectedAttraction = jdbcTemplate.update(deleteFromAttractionTable, id);

        System.out.println("Junction table attraction id deleted: " + id + ", rows affected: " + rowsAffectedJunction);

        if(rowsAffectedAttraction == 1){
            System.out.println("Deleted attraction with id:" + id + ", rows affected: " + rowsAffectedAttraction);
        } else {
            System.out.println("Could not delete from junction table");
        }

        return id;
    }

    public List<String> getAllTagNames(){
        String sql = "SELECT name FROM Tag ORDER BY name";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public boolean existsByName(String name){
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(id) FROM Attraction WHERE name = ?", Integer.class, name);

        return count != null && count > 0;
    }

    public void addTag(String tagName){
        String sql = "INSERT INTO Tag (name) VALUES (?)";
        jdbcTemplate.update(sql, tagName);
    }

    public Integer getTagIdByName(String tagName){
        String sql = "SELECT id FROM Tag WHERE name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, tagName);
        } catch (DataAccessException e)  {
            return null;
        }
    }

}


