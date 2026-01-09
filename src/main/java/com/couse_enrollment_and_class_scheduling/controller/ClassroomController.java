package com.couse_enrollment_and_class_scheduling.controller;

import com.couse_enrollment_and_class_scheduling.entity.Classroom;
import com.couse_enrollment_and_class_scheduling.service.ClassroomService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/classrooms")
public class ClassroomController {

    private final ClassroomService classroomService;

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
    public String showEditForm(
            @PathVariable Long id,
            @RequestParam(name = "returnToAdmin", defaultValue = "false") boolean returnToAdmin,
            Model model
    ) {
        Classroom classroom = classroomService.getClassroomById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid classroom Id: " + id));
        model.addAttribute("classroom", classroom);
        model.addAttribute("returnToAdmin", returnToAdmin);
        return "classrooms/edit"; 
    }

    // Handle edit classroom form submission
    @PostMapping("/edit/{id}")
    public String updateClassroom(@PathVariable Long id,
            @Valid @ModelAttribute("classroom") Classroom classroom,
            BindingResult bindingResult,
            Model model,
            @RequestParam(name = "returnToAdmin", defaultValue = "false") boolean returnToAdmin) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("returnToAdmin", returnToAdmin);
            return "classrooms/edit";
        }

        try {
            classroomService.updateClassroom(id, classroom);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("returnToAdmin", returnToAdmin);
            return "classrooms/edit";
        }

        return returnToAdmin ? "redirect:/admin/classrooms" : "redirect:/classrooms";
    }

    // Delete classroom
    @GetMapping("/delete/{id}")
    public String deleteClassroom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            classroomService.deleteClassroom(id);
            redirectAttributes.addFlashAttribute("success", "Classroom deleted successfully.");
        } catch (EmptyResultDataAccessException ex) {
            redirectAttributes.addFlashAttribute("error", "Classroom not found.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot delete this classroom because it is referenced by other records (courses/schedules)."
            );
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Delete failed: " + ex.getMessage());
        }
        return "redirect:/classrooms";
    }
}
