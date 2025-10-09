package com.example.touristguide.repository;

import com.example.touristguide.model.TouristAttraction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:h2init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AttractionRepositoryTest {
    @Autowired
    private AttractionRepository repo;

    @Test
    void shouldSelectGetAttractions(){
        List<TouristAttraction> attractionList = repo.getAttractions();

        assertThat(attractionList).isNotNull();
        assertThat(attractionList.size()).isEqualTo(2);
        assertThat(attractionList.getFirst().getName()).isEqualTo("ARoS");
        assertThat(attractionList.getLast().getName()).isEqualTo("Tivoli");
        assertThat(attractionList.getFirst().getCity()).isEqualTo("Aarhus");
        assertThat(attractionList.getLast().getCity()).isEqualTo("Copenhagen");
        assertThat(attractionList.getFirst().getSelectedTags()).contains("Kunst", "Museum");
        assertThat(attractionList.getLast().getSelectedTags()).contains("Børnevenlig", "Restaurant", "Underholdning");
    }

    @Test
    void shouldDeleteAttraction(){
        List<TouristAttraction> attractionList = repo.getAttractions();

        assertThat(attractionList).isNotNull();
        assertThat(attractionList.size()).isEqualTo(2);
        repo.deleteAttraction(1);
        List<TouristAttraction> newList = repo.getAttractions();

        assertThat(newList.size()).isEqualTo(1);
    }

    @Test void shouldInsertAttraction(){
        List<TouristAttraction> attractionList = repo.getAttractions();
        TouristAttraction attraction = new TouristAttraction();
        attraction.setId(1);
        attraction.setName("test");
        attraction.setDescription("test");
        attraction.setCity("Copenhagen");

        int cityId = repo.getCityByName(attraction.getCity());

        assertThat(attractionList).isNotNull();
        assertThat(attractionList.size()).isEqualTo(2);

        repo.addAttraction(attraction,cityId);

        List<TouristAttraction> newList = repo.getAttractions();

        assertThat(newList.size()).isEqualTo(3);
    }
}
