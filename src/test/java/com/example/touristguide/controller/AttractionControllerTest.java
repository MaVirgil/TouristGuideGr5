package com.example.touristguide.controller;

import com.example.touristguide.model.Tags;
import com.example.touristguide.model.TouristAttraction;
import com.example.touristguide.service.AttractionService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttractionController.class)
class AttractionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttractionService attractionService;

    private TouristAttraction testAttraction;
    private List<String> mockCities;

    //Setup basic objects for all Tests, less repetition in each test case
    @BeforeEach
    void setup(){
        testAttraction = new TouristAttraction("Tivoli", "Forlystelsespark", "Copenhagen", new ArrayList<>(Arrays.asList(Tags.ENTERTAINMENT, Tags.CHILDFRIENDLY)));
        mockCities = Arrays.asList("Copenhagen", "Aarhus");
    }

    /*
    * ====================================================
    *                      GET Tests
    * ====================================================
    */
    @Test
    void shouldShowAttractionList() throws Exception {
        mockMvc.perform(get("/attractions"))
                .andExpect(status().isOk())
                .andExpect(view().name("showAllAttractions"));
    }

    @Test
    void shouldShowAttractionByName() throws Exception {

        TouristAttraction attraction = new TouristAttraction();
        attraction.setName("Tivoli");

        when(attractionService.getAttractionById(1)).thenReturn(attraction);

        mockMvc.perform(get("/attractions/tivoli"))
                .andExpect(status().isOk())
                .andExpect(view().name("showAttraction"))
                .andExpect(model().attribute("byName", attraction));
    }

    @Test
    void shouldAddAttraction() throws Exception {
        Object[] expectedTags = Tags.values();

        when(attractionService.getCities()).thenReturn(mockCities);

        mockMvc.perform(get("/attractions/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("newAttractionForm"))

                //Uses isA to evaluate objects values and not their instances
                //Uses is to evaluate values
                .andExpect(model().attribute("attraction", isA(TouristAttraction.class)))
                .andExpect(model().attribute("tags", is(expectedTags)))
                .andExpect(model().attribute("cities", is(mockCities)))
                .andExpect(model().attribute("pageRef", is("newAttraction")));

        verify(attractionService).getCities();
    }

    @Test
    void shouldShowEditAttraction() throws Exception {
        final String attractionName = "Tivoli";

        //Defines the mocked objects behaviour when we run getAttractionByName and getCities (the return the testAttraction and mockCities
        when(attractionService.getAttractionById(attractionId)).thenReturn(testAttraction);
        when(attractionService.getCities()).thenReturn(mockCities);

        //Runs a get on /attractions/{name}/edit
        mockMvc.perform(get("/attractions/{name}/edit", attractionName))
                //Expects a status().isOk() return
                .andExpect(status().isOk())
                //Expects view name is updateAttractionForm
                .andExpect(view().name("updateAttractionForm"))
                //isA compares that the object is of the correct object type otherwise it would test if the two objects have the same reference (they do not)
                .andExpect(model().attribute("attraction", isA(TouristAttraction.class)))
                //is compares the object value with the expected value
                .andExpect(model().attribute("tags", is(Tags.values())))
                .andExpect(model().attribute("cities", is(mockCities)))
                .andExpect(model().attribute("pageRef", is("updateAttraction")));

        verify(attractionService).getCities();
        verify(attractionService).getAttractionById(attractionId);

    }

    @Test
    void shouldThrowIllegalArgumentException_editAttraction() throws Exception {
        final String notFoundAttraction = "NonExistingName";

        // Service returns null to simulate a non-existing name
        when(attractionService.getAttractionById(notFoundAttraction)).thenReturn(null);

        // We perform the request and catch the wrapped ServletException
        ServletException thrownException = assertThrows(
                ServletException.class,
                () -> mockMvc.perform(get("/attractions/{name}/edit", notFoundAttraction))
                        .andReturn()
        );

        //We find the root cause of the thrownException
        Throwable rootCause = thrownException.getCause();

        //We compare the rootCause to an instance of IllegalArgumentException
        assertInstanceOf(IllegalArgumentException.class, rootCause, "ServletException skulle være IllegalArgumentException og var: " + rootCause.getClass().getName());

        // We verify the mocks
        verify(attractionService, times(1)).getAttractionById(notFoundAttraction);
        verify(attractionService, never()).getCities();
    }

    @Test
    void shouldUseCustomValuePageRef_editAttraction() throws Exception {
        final String attractionName = "Tivoli";
        final String customRef = "showAttraction";

        //Defines the behaviour of the mocked object when a method is called on the object
        when(attractionService.getAttractionById(attractionId)).thenReturn(testAttraction);
        when(attractionService.getCities()).thenReturn(mockCities);

        //Performs a get, adds the custom parameter and expects the pageRef to be the customRef
        mockMvc.perform(get("/attractions/{name}/edit", attractionName)
                .param("pageRef", customRef))
                .andExpect(model().attribute("pageRef", customRef));
    }

    /*
     * ====================================================
     *                      POST Tests
     * ====================================================
     */

    @Test
    void shouldUpdateAttraction() throws Exception {
        mockMvc.perform(post("/attractions/update")
                .flashAttr("attraction", testAttraction))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/attractions"))
                .andExpect(flash().attributeCount(0));

        //Have to use any() and not eq() as we do not have an equals or hashCode() override in TouristClass
        //This simply tests whether the object is of a TouristAttraction.class and not the actual values, which is.. a compromise.
        verify(attractionService, times(1)).editAttraction(any(TouristAttraction.class));
    }
}