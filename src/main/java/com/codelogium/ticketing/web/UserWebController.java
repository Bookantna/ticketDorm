package com.codelogium.ticketing.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.codelogium.ticketing.dto.UserDTO;
import com.codelogium.ticketing.exception.ResourceNotFoundException;
import com.codelogium.ticketing.service.UserService;

import lombok.AllArgsConstructor;

/**
 * Handles all web-based (Thymeleaf) requests related to users,
 * specifically the registration and login forms.
 */
@Controller
@RequestMapping("/")
@AllArgsConstructor
public class UserWebController {

    private final UserService userService;

    public UserWebController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Maps the root, /index, and /welcome paths to the post-login welcome page.
     */
    @GetMapping({"/", "/index", "/welcome"})
    public String showWelcomePage() {
        return "welcome";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        // FIX: Ensure the UserDto object is added to the model with the expected name ("userDto")
        // This resolves the "Neither BindingResult nor plain target object..." error in Thymeleaf.
        model.addAttribute("userDto", new UserDTO());
        return "registration";
    }

    /**
     * Handles the POST request to submit the user registration form.
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("userDto") UserDTO userDto,
                               @RequestParam("inviteCode") String inviteCode,
                               RedirectAttributes redirectAttributes) {
        try {
            // Your UserServiceImp handles the password encoding and room linking
            userService.createUser(userDto.toEntity(), inviteCode);

            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
            return "redirect:/login"; // Redirects to the login page after successful registration
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed: Invalid invite code.");
            return "redirect:/register"; // Returns to the registration page on failure
        } catch (Exception e) {
            // Catch other potential exceptions (e.g., duplicate username)
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

}
