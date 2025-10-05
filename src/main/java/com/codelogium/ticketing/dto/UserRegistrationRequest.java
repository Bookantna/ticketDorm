package com.codelogium.ticketing.dto;

import com.codelogium.ticketing.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Data Transfer Object (DTO) used for user registration requests.
 * It packages the User entity details along with the non-persistent inviteCode.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {

    @Valid // Ensure validation runs on fields inside the User object
    @NotNull(message = "User details are required for registration")
    private User user;

    // This field holds the room code from the request body.
    // It can be null if the user is registering for a non-resident role.
    private String inviteCode;

    public UserRegistrationRequest(User user, String inviteCode) {
        this.user = user;
        this.inviteCode = inviteCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}
