package com.couse_enrollment_and_class_scheduling.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.couse_enrollment_and_class_scheduling.entity.Role;
import com.couse_enrollment_and_class_scheduling.entity.User;
import com.couse_enrollment_and_class_scheduling.repository.RoleRepository;
import com.couse_enrollment_and_class_scheduling.repository.UserRepository;

@Controller
public class AuthController {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role studentRole = roleRepository.findByName("ROLE_STUDENT");
        user.getRoles().add(studentRole);

        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}