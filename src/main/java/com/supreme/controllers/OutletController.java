package com.supreme.controllers;

import com.supreme.payload.request.OutletModel;
import com.supreme.services.OutletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/admin/outlet")
public class OutletController {

    private final OutletService outletService;

    @Autowired
    public OutletController(OutletService outletService) {
        this.outletService = outletService;
    }

    // Upload Outlet details and Image to S3
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addOutlet(@ModelAttribute OutletModel outletModel) {
        return outletService.addOutlet(outletModel);
    }

    // Fetch Outlet details
    @GetMapping("/details/{outletId}")
    public ResponseEntity<?> getOutletDetails(@PathVariable Long outletId) {
        return outletService.getOutletDetails(outletId);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getOutletsList() {
        return outletService.getOutletsList();
    }

    // Download Outlet Image by Outlet name
    @GetMapping("/download/{outletName}")
    public ResponseEntity<?> getOutletPicByName(@PathVariable String outletName)
            throws IOException {
        return outletService.getOutletPicByName(outletName);
    }

    // Update Outlet Details and image by deleting old one from S3 bucket
    @PutMapping(value = "/update/{outletId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateOutlet(
            @PathVariable Long outletId,
            @ModelAttribute OutletModel outletModel // @Valid
    ) {
        return outletService.updateOutlet(outletId, outletModel);
    }

    // Delete Outlet
    @DeleteMapping("/delete/{outletId}")
    public ResponseEntity<?> deleteOutlet(@PathVariable Long outletId) {
        return outletService.deleteOutlet(outletId);
    }

}
