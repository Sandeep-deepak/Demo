package com.supreme.controllers;

import com.supreme.payload.request.ExecutiveProfileModel;
import com.supreme.serviceImpl.ExecutiveProfileServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/executive")
public class ExecutiveProfileController {

    private final ExecutiveProfileServiceImpl executiveProfileServiceImpl;

    @Autowired
    public ExecutiveProfileController(ExecutiveProfileServiceImpl executiveProfileServiceImpl) {
        this.executiveProfileServiceImpl = executiveProfileServiceImpl;
    }

    // Create Executive Profile
    @PostMapping(path = "/addProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addExecutiveProfile(@ModelAttribute @Valid ExecutiveProfileModel profileRequest) throws IOException {
        return executiveProfileServiceImpl.addExecutiveProfile(profileRequest);
    }

    // Fetch Executive Profile by mobile number
    @GetMapping("/getProfile/{phNo}")
    public ResponseEntity<?> getExecutiveProfile(@PathVariable String phNo) {
        return executiveProfileServiceImpl.getExecutiveProfile(phNo);
    }

    // Update Executive Profile
    @PutMapping(path = "/updateProfile/{phNo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateExecutiveProfile(@PathVariable String phNo, @ModelAttribute ExecutiveProfileModel profileRequest) {
        return executiveProfileServiceImpl.updateExecutiveProfile(phNo, profileRequest);
    }

    // Delete Executive Profile (set profile field active as false and deleted as true)
    @DeleteMapping("/deleteProfile/{phNo}")
    public ResponseEntity<?> deleteExecutiveProfile(@PathVariable String phNo) {
        return executiveProfileServiceImpl.deleteExecutiveProfile(phNo);
    }

    // Fetch Executive Profile Image
    @GetMapping("/pic/download/{phNo}")
    public ResponseEntity<?> getExecutiveProfilePic(@PathVariable String phNo) throws IOException {
        return executiveProfileServiceImpl.getExecutiveProfilePic(phNo);
    }

    // Fetch Executive Profile by status
    @GetMapping("/profilesByStatus")
    public ResponseEntity<?> getExecutiveProfilesByStatus(@RequestParam(required = false) Boolean active, @RequestParam(required = false) Boolean deleted) {
        return executiveProfileServiceImpl.getExecutiveProfilesByStatus(active, deleted);
    }

}


