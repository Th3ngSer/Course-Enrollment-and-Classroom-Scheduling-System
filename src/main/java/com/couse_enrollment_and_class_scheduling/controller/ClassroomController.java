package com.couse_enrollment_and_class_scheduling.controller;

import com.couse_enrollment_and_class_scheduling.entity.Classroom;
import com.couse_enrollment_and_class_scheduling.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/classrooms")
public class ClassroomController {

    private final ClassroomService classroomService;

    @Autowired
    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    // List all classrooms
    @GetMapping
    public String listClassrooms(Model model) {
        List<Classroom> classrooms = classroomService.getAllClassrooms();
        model.addAttribute("classrooms", classrooms);
        return "classrooms/list"; 
    }

    // Show form to add a new classroom
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("classroom", new Classroom());
        return "classrooms/add"; 
    }

    // Handle add classroom form submission
    @PostMapping("/add")
    public String addClassroom(@Valid @ModelAttribute("classroom") Classroom classroom,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "classrooms/add";
        }

        try {
            classroomService.addClassroom(classroom);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "classrooms/add";
        }

        return "redirect:/classrooms";
    }

    // Show form to edit a classroom
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Classroom classroom = classroomService.getClassroomById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid classroom Id: " + id));
        model.addAttribute("classroom", classroom);
        return "classrooms/edit"; 
    }

    // Handle edit classroom form submission
    @PostMapping("/edit/{id}")
    public String updateClassroom(@PathVariable Long id,
            @Valid @ModelAttribute("classroom") Classroom classroom,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "classrooms/edit";
        }

        try {
            classroomService.updateClassroom(id, classroom);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "classrooms/edit";
        }

        return "redirect:/classrooms";
    }

    // Delete classroom
    @GetMapping("/delete/{id}")
    public String deleteClassroom(@PathVariable Long id) {
        classroomService.deleteClassroom(id);
        return "redirect:/classrooms";
    }
}
