package com.codelogium.ticketing.api;

import com.codelogium.ticketing.dto.UserDTO;
import com.codelogium.ticketing.dto.UserRegistrationRequest;
import com.codelogium.ticketing.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.codelogium.ticketing.entity.User;
import com.codelogium.ticketing.exception.ErrorResponse;
import com.codelogium.ticketing.service.UserService;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@Tag(name = "User Controller", description = "Manages user operations")
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid UserRegistrationRequest request) {

        User registeredUser = userService.createUser(
                request.getUser(),
                request.getInviteCode()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully with ID: " + registeredUser.getId());
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully retrieved", content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(ref = "#/components/responses/401")
    })
    @Operation(summary = "Get User", description = "Retrieves a user by ID")
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> retrieveUser(@PathVariable Long userId) {

        User userEntity = userService.retrieveUser(userId);
        UserDTO userDto = userMapper.toDTO(userEntity);

        return ResponseEntity.ok(userDto);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/401")
    })
    @Operation(summary = "Delete User", description = "Deletes a user by ID")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeUser(@PathVariable Long userId) {
        userService.removeUser(userId);
        return ResponseEntity.noContent().build();
    }
}