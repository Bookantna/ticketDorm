package com.codelogium.ticketing.service;

import com.codelogium.ticketing.entity.User;

public interface UserService {

    // Updated method signature
    User createUser(User user, String inviteCode);

    User retrieveUser(Long userId);

    User retrieveUser(String username);

    void removeUser(Long userId);
}