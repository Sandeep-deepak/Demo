package com.supreme.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/anyRole")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('DISTRIBUTOR') or hasAuthority('EXECUTIVE')")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/admin")
//  @PreAuthorize("hasRole('ADMIN')") // ROLE_ADMIN in Database
    @PreAuthorize("hasAuthority('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }

    @GetMapping("/distributor")
    @PreAuthorize("hasAuthority('DISTRIBUTOR')")
    public String distributorAccess() {
        return "DISTRIBUTOR Board.";
    }

    @GetMapping("/sales")
    @PreAuthorize("hasAuthority('EXECUTIVE')")
    public String salesAccess() {
        return "Sales Executive Board.";
    }

}
