package com.supreme.service;

import com.supreme.payload.request.DistributorProfileModel;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface DistributorProfileService {

    // Create Distributor Profile
    ResponseEntity<?> addDistributorProfile(DistributorProfileModel profileRequest);

    // Update Distributor Profile
    ResponseEntity<?> updateDistributorProfile(String phNo, DistributorProfileModel profileRequest);

    // Fetch List of Executives corresponding to Distributor
    ResponseEntity<?> getExecutives(Long distributorId);

    // Fetch Distributor Profile by status
    ResponseEntity<?> getDistributorProfilesByStatus(Boolean active, Boolean deleted);

    // Fetch Distributor Profile Image
    ResponseEntity<?> getDistributorProfile(String phNo);

    // Delete Distributor Profile (set profile field active as false and deleted as true)
    ResponseEntity<?> deleteDistributorProfile(String phNo);

    // Fetch Distributor Profile Image
    ResponseEntity<?> getDistributorProfilePic(String phNo) throws IOException;
}
