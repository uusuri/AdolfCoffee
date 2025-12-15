package com.coffee.app.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/secured")
public class MainSecurityController {
    @GetMapping("/user")
    public String userAccess(Principal principal) {
        if (principal == null) {
            return "No authenticated user";
        }
        return principal.getName();
    }
}
