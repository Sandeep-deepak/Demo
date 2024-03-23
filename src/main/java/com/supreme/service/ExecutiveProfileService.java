package com.supreme.service;

import com.supreme.payload.request.ExecutiveProfileModel;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface ExecutiveProfileService {

    // Create Executive Profile
    ResponseEntity<?> addExecutiveProfile(ExecutiveProfileModel profileRequest);

    // Update Executive Profile
    ResponseEntity<?> updateExecutiveProfile(String phNo, ExecutiveProfileModel profileRequest);

    // Fetch Executive Profile by status
    ResponseEntity<?> getExecutiveProfilesByStatus(Boolean active, Boolean deleted);

    // Fetch Executive Profile by mobile number
    ResponseEntity<?> getExecutiveProfile(String phNo);

    // Delete Profile (set profile field active as false and deleted as true)
    ResponseEntity<?> deleteExecutiveProfile(String phNo);

    // Fetch Executive Profile Image
    ResponseEntity<?> getExecutiveProfilePic(String phNo) throws IOException;

}
