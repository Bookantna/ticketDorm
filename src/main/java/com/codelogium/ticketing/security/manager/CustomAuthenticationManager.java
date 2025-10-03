package com.codelogium.ticketing.security.manager;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException; // Added
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.codelogium.ticketing.security.rbac.CustomUserDetailsServiceImp;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

    // One of these fields was null because the underlying class wasn't annotated
    private CustomUserDetailsServiceImp customUserDetailsServiceImp;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public CustomAuthenticationManager(CustomUserDetailsServiceImp customUserDetailsServiceImp, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.customUserDetailsServiceImp = customUserDetailsServiceImp;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        try {
            // If customUserDetailsServiceImp is null, it throws an exception here
            System.out.println(authentication.getPrincipal() + authentication.getName());
            UserDetails userDetails  = customUserDetailsServiceImp.loadUserByUsername(authentication.getName());

            // 1. Check password match
            if(!bCryptPasswordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
                throw new BadCredentialsException("You provided an incorrect Password");
            }

            // 2. SUCCESSFUL AUTHENTICATION FIX: Return the authenticated token
            return new UsernamePasswordAuthenticationToken(
                    userDetails, // Principal
                    null,        // Credentials (set to null)
                    userDetails.getAuthorities()
            );

        } catch (AuthenticationException e) {
            // Re-throw expected Spring Security exceptions
            throw e;
        } catch (Exception e) {
            // Catch the NullPointerException from the missing dependency
            System.err.println("FATAL INJECTION ERROR: " + e.getMessage());
            e.printStackTrace();
            // Wrap the unexpected exception as a Spring Security service error
            throw new AuthenticationServiceException("Unexpected internal service error. Check component scanning.", e);
        }
    }
}