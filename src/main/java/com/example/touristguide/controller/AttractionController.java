package com.example.touristguide.controller;

import com.example.touristguide.model.TouristAttraction;
import com.example.touristguide.service.AttractionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/")
public class AttractionController {
    private final AttractionService service;

    public AttractionController(AttractionService service) {
        this.service = service;
    }

    //mappings

    //GET
    @GetMapping
    public String getIndex(){
        return "index";
    }

    @GetMapping("/attractions")
    public String getAttractions(Model model) {
        List<TouristAttraction> touristAttractions = service.getAttractions();
        TouristAttraction attraction = new TouristAttraction();

        model.addAttribute("attractionsList", touristAttractions);
        model.addAttribute("attraction", attraction);

        return "showAllAttractions";
    }

    @GetMapping("/attractions/{id}")
    public String getAttractionsByName(@PathVariable int id, Model model){

        TouristAttraction attraction = service.getAttractionById(id);

        //Debugging
        System.out.println("Fetched attraction: " +attraction);
        System.out.println("Attraction name: " + attraction.getName());

        model.addAttribute("byName", attraction);

        return "showAttraction";
    }

    //Includes a fallback measure in case pageRef is not send required = false, defaultValue = "newAttraction"
    @GetMapping("/attractions/add")
    public String addAttraction (Model model,
                                 @RequestParam(value = "pageRef", required = false, defaultValue = "newAttraction") String pageRef) {
        TouristAttraction attractionToAdd = new TouristAttraction();

        model.addAttribute("attraction", attractionToAdd);
        model.addAttribute("tags", service.getAllTagNames());
        model.addAttribute("cities", this.service.getCities());
        model.addAttribute("pageRef", pageRef);

        return "newAttractionForm";
    }

    //Includes a fall back in case pageRef is not send required = false, defaultValue = "updateAttraction"
    @GetMapping("/attractions/{id}/edit")
    public String editAttraction(@PathVariable int id, Model model,
                                 @RequestParam(value = "pageRef", required = false, defaultValue = "updateAttraction") String pageRef) {
        TouristAttraction attraction = service.getAttractionById(id);

        if(attraction == null){
            throw new IllegalArgumentException("Attraction does not exist");
        }

        model.addAttribute("attraction", attraction);
        model.addAttribute("tags", service.getAllTagNames());
        model.addAttribute("cities", this.service.getCities());
        model.addAttribute("pageRef", pageRef);

        return "updateAttractionForm";
    }

    @GetMapping("/attractions/{id}/tags")
    public String showAttractionTags(@PathVariable int id, Model model){
        TouristAttraction attraction = service.getAttractionById(id);

        if(attraction == null){
            throw new IllegalArgumentException("Attraction does not exist");
        }

        model.addAttribute("attraction", attraction);

        return "showAttractionTags";
    }

    //POST

    @PostMapping("/attractions/save")
    public String saveAttraction(RedirectAttributes redirectAttributes, @ModelAttribute TouristAttraction attraction){

        if (service.addAttraction(attraction) == null) {
            redirectAttributes.addFlashAttribute("failedToAddAttraction", true);
        }

        return "redirect:/attractions";
    }

    @PostMapping("/attractions/update")
    public String updateAttraction(@ModelAttribute TouristAttraction attraction){

        service.editAttraction(attraction);

        return "redirect:/attractions";
    }

    @PostMapping("/attractions/delete/{id}")
    public String deleteAttraction(@PathVariable int id) {

        service.deleteAttraction(id);

        return "redirect:/attractions";
    }
}
