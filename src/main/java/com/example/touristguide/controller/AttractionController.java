package com.example.touristguide.controller;

import com.example.touristguide.model.Tags;
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

    @GetMapping("/attractions/{name}")
    public String getAttractionsByName(@PathVariable String name, Model model){

        TouristAttraction attraction = service.getAttractionByName(name);

        model.addAttribute("byName", attraction);

        return "showAttraction";
    }

    @GetMapping("/attractions/add")
    public String addAttraction (Model model) {
        TouristAttraction attractionToAdd = new TouristAttraction();

        model.addAttribute("attraction", attractionToAdd);
        model.addAttribute("tags", Tags.values());
        model.addAttribute("cities", this.service.getCities());

        return "newAttractionForm";
    }

    @GetMapping("/attractions/{name}/edit")
    public String editAttraction(@PathVariable String name, Model model) {
        TouristAttraction attraction = service.getAttractionByName(name);
        Tags[] tagList = Tags.values();

        if(attraction == null){
            throw new IllegalArgumentException("Attraction does not exist");
        }

        model.addAttribute("attraction", attraction);
        model.addAttribute("tags", tagList);
        model.addAttribute("cities", this.service.getCities());

        return "updateAttractionForm";
    }

    @GetMapping("/attractions/{name}/tags")
    public String showAttractionTags(@PathVariable String name, Model model){
        TouristAttraction attraction = service.getAttractionByName(name);

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

    @PostMapping("/attractions/delete/{name}")
    public String deleteAttraction(@PathVariable String name) {

        service.deleteAttraction(name);

        return "redirect:/attractions";
    }
}
