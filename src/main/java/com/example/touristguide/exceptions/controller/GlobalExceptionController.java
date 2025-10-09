package com.example.touristguide.exceptions.controller;

import com.example.touristguide.exceptions.service.AttractionNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(AttractionNotFoundException.class)
    public String handleAttractionNotFound(AttractionNotFoundException ex, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());

        return "redirect:/attractions";
    }
}
