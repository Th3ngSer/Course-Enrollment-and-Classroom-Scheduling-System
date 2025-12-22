package com.couse_enrollment_and_class_scheduling.controller;

import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.couse_enrollment_and_class_scheduling.entity.Role;
import com.couse_enrollment_and_class_scheduling.entity.User;
import com.couse_enrollment_and_class_scheduling.repository.RoleRepository;
import com.couse_enrollment_and_class_scheduling.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;

@Controller
public class AuthController {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, 
                         RoleRepository roleRepository, 
                         PasswordEncoder passwordEncoder) {
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
    public String register(@Valid @ModelAttribute("user") User user, 
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        
        // Validate input
        if (bindingResult.hasErrors()) {
            return "register";
        }
        
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            bindingResult.rejectValue("username", "error.user", 
                    "Username already exists");
            return "register";
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            bindingResult.rejectValue("email", "error.user", 
                    "Email already registered");
            return "register";
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Initialize roles collection
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        
        // Assign ROLE_STUDENT by default
        Optional<Role> studentRole = roleRepository.findByName("ROLE_STUDENT");
        if (studentRole.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", 
                    "System error: Default role not found");
            return "redirect:/register";
        }
        
        user.getRoles().add(studentRole.get());
        
        // Set user as active
        user.setEnabled(true);
        
        // Save user
        try {
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", 
                    "Registration successful! Please login.");
            return "redirect:/login?registered";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                    "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model, org.springframework.security.core.Authentication auth) {
        if (auth != null) {
            String roles = auth.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("No roles");
            model.addAttribute("userRoles", roles);
        }
        return "access-denied";
    }
}