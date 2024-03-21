package com.supreme.controllers;

import com.supreme.entity.AppFeatures;
import com.supreme.payload.request.AppFeaturesReq;
import com.supreme.payload.response.ErrorResponse;
import com.supreme.payload.response.Response;
import com.supreme.repository.AppFeaturesRepo;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class AppFeaturesController {

    private final Response response;
    private final ErrorResponse errorResponse;
    private final AppFeaturesRepo appFeaturesRepo;
    private final ModelMapper mapper;
    @Autowired
    public AppFeaturesController(Response response, ErrorResponse errorResponse, AppFeaturesRepo appFeaturesRepo, ModelMapper mapper) {
        this.response = response;
        this.errorResponse = errorResponse;
        this.appFeaturesRepo = appFeaturesRepo;
        this.mapper = mapper;
    }

    @PutMapping("/appFeatures")
    public ResponseEntity<?> setAppFeatures(@RequestBody AppFeaturesReq appFeaturesReq){
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        AppFeatures features = mapper.map(appFeaturesReq, AppFeatures.class);

        features.setAppFeaturesId(1L);
        features.setUseLocalImages(appFeaturesReq.isUseLocalImages());
        features.setShowTnCAgreement(appFeaturesReq.isShowTnCAgreement());
        features.setShowSubCategories(appFeaturesReq.isShowSubCategories());
        features.setShowPrice(appFeaturesReq.isShowPrice());
        features.setShowDelivery(appFeaturesReq.isShowDelivery());
        features.setAddCategory(appFeaturesReq.isAddCategory());
        features.setUpdateCategory(appFeaturesReq.isUpdateCategory());
        features.setDeleteCategory(appFeaturesReq.isDeleteCategory());
        features.setAddSubCategory(appFeaturesReq.isAddSubCategory());
        features.setUpdateSubCategory(appFeaturesReq.isUpdateSubCategory());
        features.setDeleteSubCategory(appFeaturesReq.isDeleteSubCategory());
        features.setAddProduct(appFeaturesReq.isAddProduct());
        features.setUpdateProduct(appFeaturesReq.isUpdateProduct());
        features.setDeleteProduct(appFeaturesReq.isDeleteProduct());
        features.setProfilePicSize1Mb(appFeaturesReq.isProfilePicSize1Mb());
        features.setProfilePicSize2Mb(appFeaturesReq.isProfilePicSize2Mb());
        appFeaturesRepo.save(features);

        response.setStatusCode(HttpStatus.CREATED.value());
        response.setStatus(1);
        response.setMessageCode("MSG13");
        response.setMessage("App Features Updated successfully");
        response.setResult(features);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/appFeatures")
    public ResponseEntity<?> getAppFeatures(){
        Optional<AppFeatures> appFeatures = appFeaturesRepo.findById(1L);
        if (appFeatures.isPresent()){
            response.setStatusCode(HttpStatus.OK.value());
            response.setStatus(1);
            response.setMessageCode("MSG13");
            response.setMessage("App Features fetched successfully");
            response.setResult(appFeatures);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setStatus(0);
        errorResponse.setMessageCode("MSG14");
        errorResponse.setMessage("App Features Id doesn't exists");

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
