package com.codelogium.ticketing.web;

import com.codelogium.ticketing.dto.TicketCreationRequest;
import com.codelogium.ticketing.dto.TicketDTO;
import com.codelogium.ticketing.dto.TicketRoomDTO;
import com.codelogium.ticketing.entity.Room;
import com.codelogium.ticketing.entity.Ticket;
import com.codelogium.ticketing.entity.TicketRoom;
import com.codelogium.ticketing.entity.enums.Status;
import com.codelogium.ticketing.exception.ResourceNotFoundException;
import com.codelogium.ticketing.mapper.TicketMapper;
import com.codelogium.ticketing.service.TicketService;
import com.codelogium.ticketing.service.UserRoomService;
import com.codelogium.ticketing.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * Controller to handle web requests for ticket management (displaying views).
 * Uses Thymeleaf templates instead of returning JSON data.
 */
@Controller
@RequestMapping("/tickets")
public class TicketWebController {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final UserService userService;
    private final UserRoomService userRoomService;

    public TicketWebController(TicketService ticketService, TicketMapper ticketMapper, UserService userService, UserRoomService userRoomService) {
        this.ticketService = ticketService;
        this.ticketMapper = ticketMapper;
        this.userService = userService;
        this.userRoomService = userRoomService;
    }

    /**
     * Shows the list of tickets for the currently authenticated user.
     * @param model Thymeleaf Model
     * @param authentication Spring Security Authentication object
     * @return The name of the Thymeleaf template (tickets.html)
     */
    @GetMapping("/list") // Updated mapping to use the base path /tickets/
    public String showTicketsList(Model model, Authentication authentication, HttpServletRequest request) {

        String username = "Guest (via Authentication)"; // Fallback to authentication if cookies fail
        String roles = "N/A (via Authentication)";
        String jwtToken = "No token found";

        // Variables to store cookie values
        String cookieUsername = null;
        String cookieRoles = null;
        String cookieJwtToken = null;

        // Iterate through cookies to find USER_NAME, USER_ROLES, and JWT_TOKEN
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                switch (cookie.getName()) {
                    case "JWT_TOKEN":
                        cookieJwtToken = cookie.getValue();
                        break;
                    case "USER_NAME":
                        cookieUsername = cookie.getValue();
                        break;
                    case "USER_ROLES":
                        cookieRoles = cookie.getValue();
                        break;
                }
            }
        }

        // 1. Fetch tickets for the current user
        // Ensure cookieUsername is not null before attempting to retrieve the user
        if (cookieUsername == null) {
            // Handle scenario where username cookie is missing (e.g., redirect to login or show error)
            // For now, we'll try to use the Authentication object as a fallback
            if (authentication != null && authentication.getName() != null) {
                cookieUsername = authentication.getName();
            } else {
                model.addAttribute("errorMessage", "Cannot retrieve user information.");
                return "tickets"; // Show empty list or error
            }
        }

        Long userId = Long.valueOf(userService.retrieveUser(cookieUsername).getId());



        // Pass an empty DTO to bind the form inputs to
        model.addAttribute("ticketRequest", new TicketCreationRequest());
        // Pass the user ID for hidden form fields or security checks

        List<TicketDTO> tickets = ticketService.retrieveTicketsByCreator(userId)
                .stream()
                .map(ticketMapper::toDto)
                .toList();


       model.addAttribute("tickets", tickets);
       model.addAttribute("currentUserId", userService.retrieveUser(cookieUsername).getId());

        return "tickets"; // Display the tickets.html template
    }

    /**
     * Shows the form for creating a new ticket.
     * @param model Thymeleaf Model
     * @return The name of the Thymeleaf template (create-ticket.html)
     */
    @GetMapping("/create")
    public String showCreateTicketForm(Model model, Authentication authentication, HttpServletRequest request) {

        String username = "Guest (via Authentication)"; // Fallback to authentication if cookies fail
        String roles = "N/A (via Authentication)";
        String jwtToken = "No token found";

        // Variables to store cookie values
        String cookieUsername = null;
        String cookieRoles = null;
        String cookieJwtToken = null;

        // Iterate through cookies to find USER_NAME, USER_ROLES, and JWT_TOKEN
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                switch (cookie.getName()) {
                    case "JWT_TOKEN":
                        cookieJwtToken = cookie.getValue();
                        break;
                    case "USER_NAME":
                        cookieUsername = cookie.getValue();
                        break;
                    case "USER_ROLES":
                        cookieRoles = cookie.getValue();
                        break;
                }
            }
        }

        // 1. Fetch tickets for the current user
        // Ensure cookieUsername is not null before attempting to retrieve the user
        if (cookieUsername == null) {
            // Handle scenario where username cookie is missing (e.g., redirect to login or show error)
            // For now, we'll try to use the Authentication object as a fallback
            if (authentication != null && authentication.getName() != null) {
                cookieUsername = authentication.getName();
            } else {
                model.addAttribute("errorMessage", "Cannot retrieve user information.");
                return "tickets"; // Show empty list or error
            }
        }


        // Pass an empty DTO to bind the form inputs to
        model.addAttribute("ticketRequest", new TicketCreationRequest());
        // Pass the user ID for hidden form fields or security checks

        List<Room> rooms = userRoomService.retrieveRooms(userService.retrieveUser(cookieUsername).getId());

// Convert to list of room numbers only
        List<String> roomNumbers = rooms.stream()
                .filter(Objects::nonNull)
                .map(Room::getRoomNumber)
                .toList();

        String roomNumbersJson = "[]"; // default fallback
        try {
            roomNumbersJson = new ObjectMapper().writeValueAsString(roomNumbers);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // or log.error("Error serializing room numbers", e);
        }

        model.addAttribute("roomNumbersJson", roomNumbersJson);
        return "create-ticket"; // Display the create-ticket.html template
    }

    /**
     * Handles the form submission for creating a new ticket.
     * Maps to the service layer method which eventually calls the REST API logic.
     * @param request The validated ticket creation DTO
     * @param authentication Spring Security Authentication object
     * @param redirectAttributes Used to pass messages after a redirect
     * @return Redirects to the ticket list page
     */
    @PostMapping("/create")
    public String createTicket(@ModelAttribute("ticketRequest") TicketCreationRequest formRequest,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes, HttpServletRequest request) {

        String username = "Guest (via Authentication)"; // Fallback to authentication if cookies fail
        String roles = "N/A (via Authentication)";
        String jwtToken = "No token found";

        // Variables to store cookie values
        String cookieUsername = null;
        String cookieRoles = null;
        String cookieJwtToken = null;

        // Iterate through cookies to find USER_NAME, USER_ROLES, and JWT_TOKEN
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                switch (cookie.getName()) {
                    case "JWT_TOKEN":
                        cookieJwtToken = cookie.getValue();
                        break;
                    case "USER_NAME":
                        cookieUsername = cookie.getValue();
                        break;
                    case "USER_ROLES":
                        cookieRoles = cookie.getValue();
                        break;
                }
            }
        }

        Long userId = Long.valueOf(userService.retrieveUser(cookieUsername).getId());

        try {
            formRequest.setStatus(Status.NEW);
            ticketService.createTicket(userId, formRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Ticket created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create ticket: " + e.getMessage());
            // UPDATED: Redirect back to the correct path for the create form
            return "redirect:/tickets/create";
        }

        // UPDATED: Redirect to the correct path for the list view
        return "redirect:/tickets/list";
    }

    // Example method you need in your TicketWebController

    @PostMapping("/{id}")
    public String deleteTicket(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        String username = "Guest (via Authentication)"; // Fallback to authentication if cookies fail
        String roles = "N/A (via Authentication)";
        String jwtToken = "No token found";

        // Variables to store cookie values
        String cookieUsername = null;
        String cookieRoles = null;
        String cookieJwtToken = null;

        // Iterate through cookies to find USER_NAME, USER_ROLES, and JWT_TOKEN
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                switch (cookie.getName()) {
                    case "JWT_TOKEN":
                        cookieJwtToken = cookie.getValue();
                        break;
                    case "USER_NAME":
                        cookieUsername = cookie.getValue();
                        break;
                    case "USER_ROLES":
                        cookieRoles = cookie.getValue();
                        break;
                }
            }
        }


        Long userId = Long.valueOf(userService.retrieveUser(cookieUsername).getId());

        try {
            ticketService.removeTicket(id,userId);
            redirectAttributes.addFlashAttribute("successMessage", "Ticket " + id + " deleted successfully.");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ticket not found or unauthorized to delete.");
        }
        return "redirect:/tickets/list";
    }
    @GetMapping("/details/{id}")
    public String showTicketDetails(@PathVariable Long id, Model model, Authentication authentication, HttpServletRequest request) {
        String username = "Guest (via Authentication)"; // Fallback to authentication if cookies fail
        String roles = "N/A (via Authentication)";
        String jwtToken = "No token found";

        // Variables to store cookie values
        String cookieUsername = null;
        String cookieRoles = null;
        String cookieJwtToken = null;

        // Iterate through cookies to find USER_NAME, USER_ROLES, and JWT_TOKEN
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                switch (cookie.getName()) {
                    case "JWT_TOKEN":
                        cookieJwtToken = cookie.getValue();
                        break;
                    case "USER_NAME":
                        cookieUsername = cookie.getValue();
                        break;
                    case "USER_ROLES":
                        cookieRoles = cookie.getValue();
                        break;
                }
            }
        }

        if (cookieUsername == null) {
            model.addAttribute("errorMessage", "User session expired or not found.");
            return "redirect:/tickets/list";
        }

        try {
            Long userId = Long.valueOf(userService.retrieveUser(cookieUsername).getId());

            // Retrieve the ticket and map to DTO
            TicketDTO ticket = ticketMapper.toDto(ticketService.retrieveTicket(userId, id));

            model.addAttribute("ticket", ticket);
            model.addAttribute("role", cookieRoles);
            model.addAttribute("currentUserId", userId);

        } catch (ResourceNotFoundException e) {
            model.addAttribute("errorMessage", "Ticket not found: " + e.getMessage());
            return "tickets"; // Show the list view with an error message
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while viewing ticket details: " + e.getMessage());
            return "tickets";
        }

        return "ticket-details"; // Display the new detail template
    }



}
