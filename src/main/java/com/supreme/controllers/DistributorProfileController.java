package com.supreme.controllers;

import com.supreme.payload.request.DistributorProfileModel;
import com.supreme.serviceImpl.DistributorProfileServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/distributor")
public class DistributorProfileController {

    private final DistributorProfileServiceImpl distributorProfileServiceImpl;

    @Autowired
    public DistributorProfileController(DistributorProfileServiceImpl distributorProfileServiceImpl) {
        this.distributorProfileServiceImpl = distributorProfileServiceImpl;
    }

    // Create Distributor Profile
    @PostMapping(path = "/addProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addDistributorProfile(@ModelAttribute @Valid DistributorProfileModel profileRequest) {
        return distributorProfileServiceImpl.addDistributorProfile(profileRequest);
    }

    // Fetch Distributor Profile by mobile number
    @GetMapping("/getProfile/{phNo}")
    public ResponseEntity<?> getDistributorProfile(@PathVariable String phNo) {
        return distributorProfileServiceImpl.getDistributorProfile(phNo);
    }

    // Update Distributor Profile
    @PutMapping(path = "/updateProfile/{phNo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateDistributorProfile(@PathVariable String phNo, @ModelAttribute DistributorProfileModel profileRequest) {
        return distributorProfileServiceImpl.updateDistributorProfile(phNo, profileRequest);
    }

    // Delete Distributor Profile (set profile field active as false and deleted as true)
    @DeleteMapping("/deleteProfile/{phNo}")
    public ResponseEntity<?> deleteDistributorProfile(@PathVariable String phNo) {
        return distributorProfileServiceImpl.deleteDistributorProfile(phNo);
    }

    // Fetch Distributor Profile by status
    @GetMapping("/profilesByStatus")
    public ResponseEntity<?> getDistributorProfilesByStatus(@RequestParam(required = false) Boolean active, @RequestParam(required = false) Boolean deleted) {
        return distributorProfileServiceImpl.getDistributorProfilesByStatus(active, deleted);
    }

    // Fetch Distributor Profile Image
    @GetMapping("/pic/download/{phNo}")
    public ResponseEntity<?> getDistributorProfilePic(@PathVariable String phNo) throws IOException {
        return distributorProfileServiceImpl.getDistributorProfilePic(phNo);
    }

    // Fetch List of Executives corresponding to Distributor
    @GetMapping("/getExecutives/{distributorId}")
    public ResponseEntity<?> getExecutives(@PathVariable Long distributorId) {
        return distributorProfileServiceImpl.getExecutives(distributorId);
    }

}