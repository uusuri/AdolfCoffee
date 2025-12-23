package com.coffee.app.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/secured")
public class MainSecurityController {

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String userAccess(Principal principal) {
        if (principal == null) {
            return "No authenticated user";
        }
        return "Hello, " + principal.getName() + "! You are a regular user.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess(Principal principal) {
        if (principal == null) {
            return "No authenticated user";
        }
        return "Welcome, " + principal.getName() + "! You are an administrator.";
    }
}
