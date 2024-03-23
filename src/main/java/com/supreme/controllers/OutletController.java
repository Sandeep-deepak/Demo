package com.supreme.controllers;

import com.supreme.payload.request.OutletModel;
import com.supreme.serviceImpl.OutletServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/admin/outlet")
public class OutletController {

    private final OutletServiceImpl outletServiceImpl;

    @Autowired
    public OutletController(OutletServiceImpl outletServiceImpl) {
        this.outletServiceImpl = outletServiceImpl;
    }

    // Create Outlet
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addOutlet(@ModelAttribute OutletModel outletModel) {
        return outletServiceImpl.addOutlet(outletModel);
    }

    // Fetch Outlet details
    @GetMapping("/details/{outletId}")
    public ResponseEntity<?> getOutletDetails(@PathVariable Long outletId) {
        return outletServiceImpl.getOutletDetails(outletId);
    }

    // Fetch all Outlets
    @GetMapping("/all")
    public ResponseEntity<?> getOutletsList() {
        return outletServiceImpl.getOutletsList();
    }

    // Update Outlet Details and image(deletes old one from S3 bucket)
    @PutMapping(value = "/update/{outletId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateOutlet(
            @PathVariable Long outletId,
            @ModelAttribute OutletModel outletModel // @Valid
    ) {
        return outletServiceImpl.updateOutlet(outletId, outletModel);
    }

    // Delete Outlet
    @DeleteMapping("/delete/{outletId}")
    public ResponseEntity<?> deleteOutlet(@PathVariable Long outletId) {
        return outletServiceImpl.deleteOutlet(outletId);
    }

    // Download Outlet Image by Outlet name
    @GetMapping("/download/{outletName}")
    public ResponseEntity<?> getOutletPicByName(@PathVariable String outletName)
            throws IOException {
        return outletServiceImpl.getOutletPicByName(outletName);
    }

}
