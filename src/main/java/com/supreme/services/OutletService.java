package com.supreme.services;

import com.supreme.entity.Outlet;
import com.supreme.payload.request.OutletModel;
import com.supreme.payload.response.ErrorResponse;
import com.supreme.payload.response.OutletResponse;
import com.supreme.payload.response.Response;
import com.supreme.repository.AppFeaturesRepo;
import com.supreme.repository.OutletRepo;
import com.supreme.utility.ProfileUtility;
import com.supreme.utility.S3Util;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OutletService {
    private final S3Util s3Util;
    private final ProfileUtility profileUtility;
    private final OutletRepo outletRepo;
    private final AppFeaturesRepo appFeaturesRepo;
    private final OutletResponse outletResponse;
    private final Response response;
    private final ErrorResponse errorResponse;
    @Value("${s3.outletPic}")
    private String s3FolderName;
    @Value("${path.outletPath}")
    private String outletPath;

    @Autowired
    public OutletService(S3Util s3Util, ProfileUtility profileUtility, OutletRepo outletRepo, AppFeaturesRepo appFeaturesRepo, OutletResponse outletResponse, Response response, ErrorResponse errorResponse) {
        this.s3Util = s3Util;
        this.profileUtility = profileUtility;
        this.outletRepo = outletRepo;
        this.appFeaturesRepo = appFeaturesRepo;
        this.outletResponse = outletResponse;
        this.response = response;
        this.errorResponse = errorResponse;
    }

    public ResponseEntity<?> addOutlet(OutletModel outletModel) {
//        // Check if AppFeatures exists
//        Optional<AppFeatures> appFeaturesOptional = appFeaturesRepo.findById(1L);
//        if (appFeaturesOptional.isEmpty()) {
//            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "App Features doesn't exist", "MSG33"));
//        }
//        AppFeatures appFeatures = appFeaturesOptional.get();

        // Check if Outlet Name already exists
        if (Boolean.TRUE.equals(outletRepo.existsByOutletName(outletModel.getOutletName()))) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Outlet Name already exists", "MSG33"));
        }
        // Check if Outlet Name is provided and not empty
        if (outletModel.getOutletName() != null && outletModel.getOutletName().isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Outlet Name must not be null", "MSG33"));
        }
        // Check if Outlet Mobile Number is provided and not empty
        if (outletModel.getMobileNumber() != null && outletModel.getMobileNumber().isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Outlet Mobile Number must not be null", "MSG33"));
        }
        // Check if Outlet image is provided
        if (outletModel.getOutletImage().isEmpty() || outletModel.getOutletImage() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Please attach the Outlet Image", "MSG33"));
        }
        if (outletModel.getOutletAddress() != null && outletModel.getOutletAddress().isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Outlet Address must not be null", "MSG33"));
        }

        // Save Outlet
        Outlet outlet = new Outlet();
        outlet.setOutletName(outletModel.getOutletName());
        outlet.setMobileNumber(outletModel.getMobileNumber());
        outlet.setOutletAddress(outletModel.getOutletAddress());
        try {
            // Save image file here
            String s3Url = s3Util.uploadFile(s3FolderName, outletModel.getOutletImage());
            String fileName = s3Url.substring(s3Url.lastIndexOf("/") + 1);
            // Storing only URI because of frequent change in URL
            String imageDownloadUri = outletPath + outletModel.getOutletName();

            outlet.setOutletImgName(fileName);
            outlet.setOutletImgUrl(imageDownloadUri);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "Failed to save image", "MSG33"));
        }

        outletRepo.save(outlet);

        outletResponse.setOutletId(outlet.getOutletId());
        outletResponse.setOutletName(outlet.getOutletName());
        outletResponse.setMobileNumber(outlet.getMobileNumber());
        outletResponse.setAddress(outlet.getOutletAddress());
        outletResponse.setOutletImgName(outlet.getOutletImgName());
        outletResponse.setOutletImgUrl(profileUtility.generateDownloadUrl(outletPath, outlet.getOutletName()));

        return ResponseEntity.ok().body(new Response(HttpStatus.OK.value(), 1, "Outlet added successfully", "MSG17", outletResponse));
    }

    public ResponseEntity<?> getOutletDetails(Long outletId) {
        Optional<Outlet> outletOptional = outletRepo.findById(outletId);
        if (outletOptional.isPresent()) {
            Outlet outlet = outletOptional.get();

            if (outlet.getOutletImgName() != null && outlet.getOutletImgUrl() != null) {
                outletResponse.setOutletImgName(outlet.getOutletImgName());
                outletResponse.setOutletImgUrl(profileUtility.generateDownloadUrl(outletPath, outlet.getOutletName()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), 0, "Outlet Picture Not Found", "MSG27"));
            }
            outletResponse.setOutletId(outlet.getOutletId());
            outletResponse.setOutletName(outlet.getOutletName());
            outletResponse.setMobileNumber(outlet.getMobileNumber());
            outletResponse.setAddress(outlet.getOutletAddress());

            response.setStatusCode(HttpStatus.OK.value());
            response.setStatus(1);
            response.setMessageCode("MSG17");
            response.setMessage("Outlet Details fetched successfully");
            response.setResult(outletResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            errorResponse.setStatus(0);
            errorResponse.setMessageCode("MSG48");
            errorResponse.setMessage("Outlet Doesn't exists");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getOutletsList() {
        List<Outlet> outlets = outletRepo.findAll();
        outlets.stream().filter(outlet -> outlet.getOutletImgName() != null && outlet.getOutletImgUrl() != null)
                .forEach(outlet -> outlet.setOutletImgUrl(profileUtility.generateDownloadUrl(outletPath, outlet.getOutletName())));

        response.setStatusCode(HttpStatus.OK.value());
        response.setStatus(1);
        response.setMessageCode("MSG16");
        response.setMessage("Outlets fetched successfully");
        response.setResult(outlets);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> getOutletPicByName(String outletName) throws IOException {
        Optional<Outlet> outletOptional = outletRepo.findByOutletName(outletName);
        if (outletOptional.isPresent()) {
            Outlet outlet = outletOptional.get();
            if (outlet.getOutletImgName() != null && outlet.getOutletImgUrl() != null) {
                return s3Util.getImageFromS3Bucket(s3FolderName, outlet.getOutletImgName());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), 0, "Image Not Found", "MSG26"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), 0, "Outlet Doesn't exists", "MSG11"));
        }
    }

    // Update Outlet Details and image by deleting old one from S3 Bucket
    public ResponseEntity<?> updateOutlet(Long outletId, OutletModel outletModel) {
        // Check if Outlet exists
        Optional<Outlet> outletOptional = outletRepo.findById(outletId);
        if (outletOptional.isEmpty()) {
            errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            errorResponse.setStatus(0);
            errorResponse.setMessageCode("MSG48");
            errorResponse.setMessage("Outlet doesn't exists");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        Outlet outlet = outletOptional.get();

        if (outletModel.getOutletName() != null && !outletModel.getOutletName().isBlank()) {
            // Check if Outlet Name already exists
            if (Boolean.TRUE.equals(outletRepo.existsByOutletName(outletModel.getOutletName()))) {
                return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Outlet Name already exists", "MSG33"));
            }
            outlet.setOutletName(outletModel.getOutletName());
        }
        if (outletModel.getMobileNumber() != null && !outletModel.getMobileNumber().isBlank()) {
            outlet.setMobileNumber(outletModel.getMobileNumber());
        }
        if (outletModel.getOutletAddress() != null && !outletModel.getOutletAddress().isBlank()) {
            outlet.setOutletAddress(outletModel.getOutletAddress());
        }
        if (outletModel.getOutletImage() != null && !outletModel.getOutletImage().isEmpty()) {
            if (outlet.getOutletImgUrl() != null && outlet.getOutletImgName() != null) {
                s3Util.deleteFileFromS3Bucket(s3FolderName, outlet.getOutletImgName());
                outlet.setOutletImgName(null);
                outlet.setOutletImgUrl(null);
            }
            String s3Url = s3Util.uploadFile(s3FolderName, outletModel.getOutletImage());
            String fileName = s3Url.substring(s3Url.lastIndexOf("/") + 1);
            String imageDownloadUri = outletPath + outlet.getOutletImgName();
            outlet.setOutletImgName(fileName);
            outlet.setOutletImgUrl(imageDownloadUri);
        }

        outletRepo.save(outlet);

        outletResponse.setOutletId(outlet.getOutletId());
        outletResponse.setOutletName(outlet.getOutletName());
        outletResponse.setMobileNumber(outlet.getMobileNumber());
        outletResponse.setAddress(outlet.getOutletAddress());
        outletResponse.setOutletImgName(outlet.getOutletImgName());
        outletResponse.setOutletImgUrl(profileUtility.generateDownloadUrl(outletPath, outlet.getOutletName()));

        response.setStatusCode(HttpStatus.OK.value());
        response.setStatus(1);
        response.setMessageCode("MSG13");
        response.setMessage("Outlet updated successfully");
        response.setResult(outletResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Delete Outlet by Id
    public ResponseEntity<?> deleteOutlet(Long outletId) {
        Optional<Outlet> outletOptional = outletRepo.findById(outletId);
        if (outletOptional.isEmpty()) {
            errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            errorResponse.setStatus(0);
            errorResponse.setMessageCode("MSG48");
            errorResponse.setMessage("Outlet doesn't exists");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        Outlet outlet = outletOptional.get();

        if (outlet.getOutletImgUrl() != null && outlet.getOutletImgName() != null) {
            s3Util.deleteFileFromS3Bucket(s3FolderName, outlet.getOutletImgName());
            outlet.setOutletImgName(null);
            outlet.setOutletImgUrl(null);
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ErrorResponse(HttpStatus.OK.value(), 0, "Image Not Found", "MSG26"));
        }
        outletRepo.deleteById(outletId);

        response.setStatusCode(HttpStatus.NO_CONTENT.value());
        response.setStatus(1);
        response.setMessageCode("MSG13");
        response.setMessage("Outlet deleted successfully");
        response.setResult(null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
