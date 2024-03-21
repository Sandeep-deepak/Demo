package com.supreme.controllers;

import com.supreme.payload.request.ExecutiveProfileModel;
import com.supreme.services.ExecutiveProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/executive")
public class ExecutiveProfileController {
    private final ExecutiveProfileService executiveProfileService;

    @Autowired
    public ExecutiveProfileController(ExecutiveProfileService executiveProfileService) {
        this.executiveProfileService = executiveProfileService;
    }

    // Creating Executive Profile
    @PostMapping(path = "/addProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addExecutiveProfile(@ModelAttribute @Valid ExecutiveProfileModel profileRequest) throws IOException {
        return executiveProfileService.addExecutiveProfile(profileRequest);
    }

    // Updating Executive Profile
    @PutMapping(path = "/updateProfile/{phNo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateExecutiveProfile(@PathVariable String phNo, @ModelAttribute ExecutiveProfileModel profileRequest) {
        return executiveProfileService.updateExecutiveProfile(phNo, profileRequest);
    }

    @GetMapping("/getProfile/{phNo}")
    public ResponseEntity<?> getExecutiveProfile(@PathVariable String phNo) {
        return executiveProfileService.getExecutiveProfile(phNo);
    }

    @GetMapping("/profilesByStatus")
    public ResponseEntity<?> getExecutiveProfilesByStatus(@RequestParam(required = false) Boolean active, @RequestParam(required = false) Boolean deleted) {
        return executiveProfileService.getExecutiveProfilesByStatus(active, deleted);
    }

    // Delete Profile (setting profile field active as false and deleted as true)
    @DeleteMapping("/deleteProfile/{phNo}")
    public ResponseEntity<?> deleteExecutiveProfile(@PathVariable String phNo) {
        return executiveProfileService.deleteExecutiveProfile(phNo);
    }

    // Get Executive Profile Picture
    @GetMapping("/pic/download/{phNo}")
    public ResponseEntity<?> getExecutiveProfilePic(@PathVariable String phNo) throws IOException {
        return executiveProfileService.getExecutiveProfilePic(phNo);
    }
}


