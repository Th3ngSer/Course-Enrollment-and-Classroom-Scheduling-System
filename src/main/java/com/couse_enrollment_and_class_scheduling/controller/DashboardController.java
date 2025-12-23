package com.couse_enrollment_and_class_scheduling.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // These values are sent to layout.html
        model.addAttribute("title", "Dashboard");
        model.addAttribute("content", "dashboard/dashboard");

        // layout.html is the main page
        return "layout";
    }
}

