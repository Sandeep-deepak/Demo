package com.supreme.controllers;

import com.supreme.payload.request.DistributorProfileModel;
import com.supreme.services.DistributorProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/distributor")
public class DistributorProfileController {
    private final DistributorProfileService distributorProfileService;

    @Autowired
    public DistributorProfileController(DistributorProfileService distributorProfileService) {
        this.distributorProfileService = distributorProfileService;
    }

    // Creating Distributor Profile
    @PostMapping(path = "/addProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addDistributorProfile(@ModelAttribute @Valid DistributorProfileModel profileRequest) {
        return distributorProfileService.addDistributorProfile(profileRequest);
    }

    // Updating Distributor Profile
    @PutMapping(path = "/updateProfile/{phNo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateDistributorProfile(@PathVariable String phNo, @ModelAttribute DistributorProfileModel profileRequest) {
        return distributorProfileService.updateDistributorProfile(phNo, profileRequest);
    }

    @GetMapping("/getExecutives/{distributorId}")
    public ResponseEntity<?> getExecutives(@PathVariable Long distributorId) {
        return distributorProfileService.getExecutives(distributorId);
    }

    @GetMapping("/getProfile/{phNo}")
    public ResponseEntity<?> getDistributorProfile(@PathVariable String phNo) {
        return distributorProfileService.getDistributorProfile(phNo);
    }

    @GetMapping("/profilesByStatus")
    public ResponseEntity<?> getDistributorProfilesByStatus(@RequestParam(required = false) Boolean active, @RequestParam(required = false) Boolean deleted) {
        return distributorProfileService.getDistributorProfilesByStatus(active, deleted);
    }

    // Delete Profile (setting profile field active as false and deleted as true)
    @DeleteMapping("/deleteProfile/{phNo}")
    public ResponseEntity<?> deleteDistributorProfile(@PathVariable String phNo) {
        return distributorProfileService.deleteDistributorProfile(phNo);
    }

    // Get Distributor Profile Picture
    @GetMapping("/pic/download/{phNo}")
    public ResponseEntity<?> getDistributorProfilePic(@PathVariable String phNo) throws IOException {
        return distributorProfileService.getDistributorProfilePic(phNo);
    }
}