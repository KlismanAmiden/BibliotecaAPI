package com.backend.Biblioteca.infrastructure.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUser {

    public String getEmail(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    public boolean isAdmin(){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
