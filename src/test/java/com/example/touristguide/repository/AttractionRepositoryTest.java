package com.example.touristguide.repository;

import com.example.touristguide.model.TouristAttraction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:h2init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AttractionRepositoryTest {
    @Autowired
    private AttractionRepository repo;

    @Test
    void selectAll(){
        List<TouristAttraction> attractionList = repo.getAttractions();

        assertThat(attractionList).isNotNull();

    }
}
