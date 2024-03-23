package com.supreme.service;

import com.supreme.payload.request.OutletModel;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface OutletService {

    // Create Outlet
    ResponseEntity<?> addOutlet(OutletModel outletModel);

    // Fetch Outlet details
    ResponseEntity<?> getOutletDetails(Long outletId);

    // Fetch all Outlets
    ResponseEntity<?> getOutletsList();

    // Update Outlet Details and image(deletes old one from S3 bucket)
    ResponseEntity<?> updateOutlet(Long outletId, OutletModel outletModel);

    // Delete Outlet by Id
    ResponseEntity<?> deleteOutlet(Long outletId);

    // Download Outlet Image by Outlet name
    ResponseEntity<?> getOutletPicByName(String outletName) throws IOException;

}
