package com.codelogium.ticketing.web;


import com.codelogium.ticketing.dto.RoomDTO;
import com.codelogium.ticketing.dto.TicketCreationRequest;
import com.codelogium.ticketing.dto.UserDTO;
import com.codelogium.ticketing.dto.UserRoomDTO;
import com.codelogium.ticketing.entity.Room;
import com.codelogium.ticketing.entity.RoomDetails;
import com.codelogium.ticketing.exception.ResourceNotFoundException;
import com.codelogium.ticketing.mapper.TicketMapper;
import com.codelogium.ticketing.service.RoomDetailService;
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

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/room")
public class RoomWebController {

    private final RoomDetailService roomDetailService;
    private final UserService userService;
    private final UserRoomService userRoomService;


    public RoomWebController(RoomDetailService roomDetailService, UserService userService, UserRoomService userRoomService) {
        this.roomDetailService = roomDetailService;
        this.userService = userService;
        this.userRoomService = userRoomService;
    }

    @GetMapping("/list")
    public String ListAllRoom(Model model, Authentication authentication, HttpServletRequest request){
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

        // Assign values to the model: Prioritize cookie values
        model.addAttribute("username",
                cookieUsername != null ? cookieUsername : (authentication != null ? authentication.getName() : "Guest"));

        model.addAttribute("role",
                cookieRoles != null ? cookieRoles : (authentication != null ? authentication.getAuthorities().toString() : "N/A"));

        model.addAttribute("jwtToken",
                cookieJwtToken != null ? cookieJwtToken : "No token found");

        Long userId = Long.valueOf(userService.retrieveUser(cookieUsername).getId());

        List<Room> rooms = userRoomService.retrieveRooms(userId);

        model.addAttribute("rooms", rooms);
        model.addAttribute("role", cookieRoles);
        model.addAttribute("currentUserId", userService.retrieveUser(cookieUsername).getId());

        return "rooms";

    }

    @GetMapping("/details/{roomId}")
    public String ShowRoomDetail(@PathVariable Long roomId, Model model, Authentication authentication, HttpServletRequest request) {

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

        // Assign values to the model: Prioritize cookie values
        model.addAttribute("username",
                cookieUsername != null ? cookieUsername : (authentication != null ? authentication.getName() : "Guest"));

        model.addAttribute("role",
                cookieRoles != null ? cookieRoles : (authentication != null ? authentication.getAuthorities().toString() : "N/A"));

        model.addAttribute("jwtToken",
                cookieJwtToken != null ? cookieJwtToken : "No token found");

        Long userId = Long.valueOf(userService.retrieveUser(cookieUsername).getId());

        List<Room> rooms = userRoomService.retrieveRooms(userId);
        Room room = rooms.stream()
                .filter(x -> x.getId().equals(roomId))
                .findFirst()
                .orElse(null);


        RoomDetails roomDetails = roomDetailService.retrieveRoomDetailByRoomId(roomId);

        model.addAttribute("room", room);
        model.addAttribute("roomDetails", roomDetails);
        model.addAttribute("role", cookieRoles);
        model.addAttribute("currentUserId", userService.retrieveUser(cookieUsername).getId());

        return "room-detail";
    }

    @GetMapping("/add")
    public String addRoomForm(Model model) {
        model.addAttribute("userDto", new UserDTO());
        return "add-room";
    }

    @PostMapping("/add")
    public String addRoom(@RequestParam("inviteCode") String inviteCode,
                               RedirectAttributes redirectAttributes, HttpServletRequest request, Model model, Authentication authentication) {

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

        // Assign values to the model: Prioritize cookie values
        model.addAttribute("username",
                cookieUsername != null ? cookieUsername : (authentication != null ? authentication.getName() : "Guest"));

        model.addAttribute("role",
                cookieRoles != null ? cookieRoles : (authentication != null ? authentication.getAuthorities().toString() : "N/A"));

        model.addAttribute("jwtToken",
                cookieJwtToken != null ? cookieJwtToken : "No token found");

        try {
            userService.updateUser(userService.retrieveUser(cookieUsername), inviteCode);

            redirectAttributes.addFlashAttribute("successMessage", "Add Room successful!");
            return "redirect:/room/list"; // Redirects to the login page after successful registration
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Add Room failed: Invalid invite code.");
            return "redirect:/room/list"; // Returns to the registration page on failure
        } catch (Exception e) {
            // Catch other potential exceptions (e.g., duplicate username)
            redirectAttributes.addFlashAttribute("errorMessage", "Add Room failed: " + e.getMessage());
            return "redirect:/room/list";
        }
    }


}
