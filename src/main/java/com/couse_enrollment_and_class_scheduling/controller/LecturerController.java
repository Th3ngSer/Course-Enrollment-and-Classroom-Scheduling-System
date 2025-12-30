package com.couse_enrollment_and_class_scheduling.controller;

import com.couse_enrollment_and_class_scheduling.entity.Lecturer;
import com.couse_enrollment_and_class_scheduling.service.LecturerService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/lecturers")
public class LecturerController {

    private final LecturerService lecturerService;

    public LecturerController(LecturerService lecturerService) {
        this.lecturerService = lecturerService;
    }

    // List all lecturers
    @GetMapping
    public String listLecturers(Model model) {
        model.addAttribute("lecturers", lecturerService.getAllLecturers());
        return "lecturers/list-lecturers";
    }

    // Show add lecturer form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("lecturer", new Lecturer());
        return "lecturers/add-lecturer";
    }

    // Handle add lecturer
    @PostMapping("/add")
    public String addLecturer(
            @Valid @ModelAttribute("lecturer") Lecturer lecturer,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            return "lecturers/add-lecturer";
        }

        lecturerService.saveLecturer(lecturer);
        return "redirect:/lecturers";
    }

    // Show edit lecturer form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("lecturer", lecturerService.getLecturerById(id));
        return "lecturers/edit-lecturer";
    }

    // Handle edit lecturer
    @PostMapping("/edit/{id}")
    public String updateLecturer(
            @PathVariable Long id,
            @Valid @ModelAttribute("lecturer") Lecturer lecturer,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            return "lecturers/edit-lecturer";
        }

        lecturer.setId(id);
        lecturerService.saveLecturer(lecturer);
        return "redirect:/lecturers";
    }

    // Delete lecturer
    @GetMapping("/delete/{id}")
    public String deleteLecturer(@PathVariable Long id) {
        lecturerService.deleteLecturer(id);
        return "redirect:/lecturers";
    }
}
