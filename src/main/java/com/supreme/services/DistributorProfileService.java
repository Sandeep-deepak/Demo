package com.supreme.services;

import com.supreme.entity.DistributorProfile;
import com.supreme.entity.ERole;
import com.supreme.entity.ProductsNewQty;
import com.supreme.entity.User;
import com.supreme.payload.request.DistributorProfileModel;
import com.supreme.payload.response.*;
import com.supreme.repository.DistributorProfileRepo;
import com.supreme.repository.ProductRepo;
import com.supreme.repository.ProductsNewQtyRepo;
import com.supreme.repository.UserRepository;
import com.supreme.utility.ProfileUtility;
import com.supreme.utility.S3Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DistributorProfileService {

    private final ProfileUtility profileUtility;
    private final S3Util s3Util;
    private final UserRepository userRepository;
    private final DistributorProfileRepo distributorProfileRepo;
    private final ProductRepo productRepo;
    private final ProductsNewQtyRepo productsNewQtyRepo;
    //    private final AppFeaturesRepo appFeaturesRepo;
    private final Response response;
    private final ErrorResponse errorResponse;
    private final DistributorProfileRespo distributorProfResponse;
    private final ProductService productService;
    private final PasswordEncoder encoder;
    @Value("${s3.distributorProfilePic}")
    private String s3FolderName;
    @Value("${path.distributor}")
    private String distributorPath;
    @Value("${path.executive}")
    private String executivePath;

    @Autowired
    public DistributorProfileService(ProfileUtility profileUtility, S3Util s3Util, UserRepository userRepository, DistributorProfileRepo distributorProfileRepo, ProductRepo productRepo, ProductsNewQtyRepo productsNewQtyRepo, Response response, ErrorResponse errorResponse, DistributorProfileRespo distributorProfResponse, ProductService productService, PasswordEncoder encoder) {
        this.profileUtility = profileUtility;
        this.s3Util = s3Util;
        this.userRepository = userRepository;
        this.distributorProfileRepo = distributorProfileRepo;
        this.productRepo = productRepo;
        this.productsNewQtyRepo = productsNewQtyRepo;
        this.response = response;
        this.errorResponse = errorResponse;
        this.distributorProfResponse = distributorProfResponse;
        this.productService = productService;
        this.encoder = encoder;
    }

    // Creating Distributor Profile
    public ResponseEntity<?> addDistributorProfile(DistributorProfileModel profileRequest) {
        Optional<User> userOptional = userRepository.findByMobileNumber(profileRequest.getMobileNumber());
//        Optional<AppFeatures> appFeatures = appFeaturesRepo.findById(1L);
        Optional<DistributorProfile> distributorProfileOptional = distributorProfileRepo.findByMobileNumber(profileRequest.getMobileNumber());

        if (userOptional.isEmpty() && distributorProfileOptional.isEmpty()) {
            User user = new User(profileRequest.getMobileNumber(),
                    encoder.encode(profileRequest.getPin()), ERole.DISTRIBUTOR);

            DistributorProfile profile = new DistributorProfile();
            profile.setFirstName(profileRequest.getFirstName());
            profile.setLastName(profileRequest.getLastName());
            profile.setMobileNumber(profileRequest.getMobileNumber());
            profile.setActive(profileRequest.getActive());
            profile.setDeleted(false);

            String downloadUrl = null;
            String downloadUri = distributorPath + profileRequest.getMobileNumber();

            MultipartFile file = profileRequest.getProfilePic();
            if (file != null && !file.isEmpty()) {
                String s3Url = s3Util.uploadFile(s3FolderName, file);
                String fileName = s3Url.substring(s3Url.lastIndexOf("/") + 1);
                profile.setProfilePicName(fileName);
                profile.setProfilePicUrl(downloadUri);
                downloadUrl = profileUtility.generateDownloadUrl(distributorPath, profileRequest.getMobileNumber());
            }

            List<ProductDTO> productsDtoList = profileUtility.mapProductsToDTOs(productService.getProductsList(), 0, 0, 0, 0, 0);
            String jsonString = profileUtility.convertListToJsonString(productsDtoList);
            profile.setProductsJson(jsonString);

            user.setDistributorProfile(profile);
            userRepository.save(user);
            profile.setUser(user);
            distributorProfileRepo.save(profile);

            productsDtoList.stream().filter(productDTO -> productDTO.getProductImgName() != null && productDTO.getProductImgUrl() != null)
                    .forEach(productDTO -> productDTO.setProductImgUrl(profileUtility.generateDownloadUrl("/admin/product/download/", productDTO.getProductName())));

            DistributorProfileRespo distributorProfile = profileUtility.mapDistributorProfileDB(profile, productsDtoList, downloadUrl);
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setStatus(1);
            response.setMessageCode("MSG1");
            response.setMessage("Distributor Profile added successfully");
            response.setResult(distributorProfile);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Phone number already exists", "MSG25"));
        }
    }

    public ResponseEntity<?> updateDistributorProfile(String phNo, DistributorProfileModel profileRequest) {
        Optional<User> userOptional = userRepository.findByMobileNumber(phNo);
//        Optional<AppFeatures> appFeatures = appFeaturesRepo.findById(1L);
        Optional<DistributorProfile> distributorProfileOptional = distributorProfileRepo.findByMobileNumber(phNo);

        if (Boolean.TRUE.equals(userRepository.existsByMobileNumber(profileRequest.getMobileNumber()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Mobile number already exists", "MSG25"));
        }
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "User doesn't Exists", "MSG25"));
        }
        User user = userOptional.get();

        if (distributorProfileOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Distributor doesn't Exists", "MSG25"));
        }
        DistributorProfile profile = distributorProfileOptional.get();

        if (profileRequest.getFirstName() != null && !profileRequest.getFirstName().isBlank()) {
            profile.setFirstName(profileRequest.getFirstName());
        }
        if (profileRequest.getLastName() != null && !profileRequest.getLastName().isBlank()) {
            profile.setLastName(profileRequest.getLastName());
        }
        // Modifying Mobile number?
        if (profileRequest.getMobileNumber() != null && !profileRequest.getMobileNumber().isBlank()) {
            profile.setMobileNumber(profileRequest.getMobileNumber());
            user.setMobileNumber(profileRequest.getMobileNumber());
        }
        if (profileRequest.getActive() != null) {
            profile.setActive(profileRequest.getActive());
        }
        if (profileRequest.getPin() != null && !profileRequest.getPin().isBlank()) {
            user.setPin(encoder.encode(profileRequest.getPin()));
        }
        profile.setDeleted(false);

        String downloadUri = distributorPath + profileRequest.getMobileNumber();
        String downloadUrl = profileUtility.generateDownloadUrl(distributorPath, profile.getMobileNumber(), profile.getProfilePicName(), profile.getProfilePicUrl());

        MultipartFile file = profileRequest.getProfilePic();
        if (file != null && !file.isEmpty()) {
            if (profile.getProfilePicName() != null && profile.getProfilePicUrl() != null) {
                s3Util.deleteFileFromS3Bucket(s3FolderName, profile.getProfilePicName());
                profile.setProfilePicName(null);
                profile.setProfilePicUrl(null);
            }
            String s3Url = s3Util.uploadFile(s3FolderName, file);
            String fileName = s3Url.substring(s3Url.lastIndexOf("/") + 1);
            profile.setProfilePicName(fileName);
            profile.setProfilePicUrl(downloadUri);
            downloadUrl = profileUtility.generateDownloadUrl(distributorPath, profileRequest.getMobileNumber());
        }

        List<ProductDTO> dBProductsList = profileUtility.convertJsonStringToList(profile.getProductsJson(), ProductDTO.class);

        List<ProductDTO> updatedDBProductsList = new ArrayList<>();
        for (ProductDTO product : dBProductsList) {
            switch (product.getProductName()) {
                case "Pan Masala":
                    if (profileRequest.getPanMasalaQty() != null) {
                        product.setNewQuantity(profileRequest.getPanMasalaQty());
                        // Current Qty(Total) = Current Qty + New Qty
                        product.setCurrentQuantity(product.getCurrentQuantity() + profileRequest.getPanMasalaQty());
                    }
                    break;
                case "Coriander Powder":
                    if (profileRequest.getCorianderQty() != null) {
                        product.setNewQuantity(profileRequest.getCorianderQty());
                        product.setCurrentQuantity(product.getCurrentQuantity() + profileRequest.getCorianderQty());
                    }
                    break;
                case "Mutton Masala":
                    if (profileRequest.getMuttonMasalaQty() != null) {
                        product.setNewQuantity(profileRequest.getMuttonMasalaQty());
                        product.setCurrentQuantity(product.getCurrentQuantity() + profileRequest.getMuttonMasalaQty());
                    }
                    break;
                case "Chicken Masala":
                    if (profileRequest.getChickenMasalaQty() != null) {
                        product.setNewQuantity(profileRequest.getChickenMasalaQty());
                        product.setCurrentQuantity(product.getCurrentQuantity() + profileRequest.getChickenMasalaQty());
                    }
                    break;
                case "Chilli Powder":
                    if (profileRequest.getChilliPowderQty() != null) {
                        product.setNewQuantity(profileRequest.getChilliPowderQty());
                        product.setCurrentQuantity(product.getCurrentQuantity() + profileRequest.getChilliPowderQty());
                    }
                    break;
                default:
                    // Default behavior if product name doesn't match any condition
                    break;
            }
            updatedDBProductsList.add(product);
        }

        String jsonString = profileUtility.convertListToJsonString(updatedDBProductsList);
        profile.setProductsJson(jsonString);

        updatedDBProductsList.stream().filter(productDTO -> productDTO.getProductImgName() != null && productDTO.getProductImgUrl() != null)
                .forEach(productDTO -> productDTO.setProductImgUrl(profileUtility.generateDownloadUrl("/admin/product/download/", productDTO.getProductName())));

        ProductsNewQty productsNewQty = new ProductsNewQty();
        productsNewQty.setAddedDate(LocalDateTime.now());
        productsNewQty.setDistributorProfile(profile);
        productsNewQty.setProductsJson(profile.getProductsJson());

        userRepository.save(user);
        distributorProfileRepo.save(profile);
        productsNewQtyRepo.save(productsNewQty);

        DistributorProfileRespo distributorProfile = profileUtility.mapDistributorProfileDB(profile, updatedDBProductsList, downloadUrl);
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setStatus(1);
        response.setMessageCode("MSG1");
        response.setMessage("Distributor Profile Updated Successfully");
        response.setResult(distributorProfile);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Fetch all executive profiles under distributor
    public ResponseEntity<?> getExecutives(Long distributorId) {
        Optional<DistributorProfile> distributorOptional = distributorProfileRepo.findById(distributorId);
        if (distributorOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Distributor doesn't Exists", "MSG25"));
        }
        List<ExecutiveProfileResponse> executivesList = profileUtility.mapExecutiveProfiles(distributorOptional.get().getExecutiveProfiles(), executivePath);

        response.setStatusCode(HttpStatus.OK.value());
        response.setStatus(1);
        response.setMessageCode("MSG16");
        response.setMessage("Executive Profile list fetched successfully");
        response.setResult(executivesList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> getDistributorProfilesByStatus(Boolean active, Boolean deleted) {
        // Fetch all profiles when active and deleted parameters are not passed
        if (active == null && deleted == null) {
            List<DistributorProfile> profileList = distributorProfileRepo.findAll();
            response.setStatusCode(HttpStatus.OK.value());
            response.setStatus(1);
            response.setMessageCode("MSG16");
            response.setMessage("Distributor Profile list fetched successfully");
            response.setResult(profileUtility.mapDistributorProfiles(profileList, distributorPath));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // Fetch profiles based on active and deleted status if provided
        List<DistributorProfile> filteredProfiles = new ArrayList<>();
        if (active != null && deleted != null) {
            filteredProfiles = distributorProfileRepo.findByActiveAndDeleted(active, deleted);
        } else if (active != null) {
            filteredProfiles = distributorProfileRepo.getDistributorProfileByActive(active);
        } else if (deleted != null) {
            filteredProfiles = distributorProfileRepo.getDistributorProfileByDeleted(deleted);
        }

        String message;
        if (active != null && !active) {
            message = "Inactive Distributor Profile list fetched successfully";
        } else if (deleted != null && deleted.equals(Boolean.TRUE)) {
            message = "Deleted Distributor Profile list fetched successfully";
        } else if (active != null) { //&& active
            message = "Active Distributor Profile list fetched successfully";
        } else {
            message = "Distributor Profile list fetched successfully";
        }

        List<DistributorProfileRespo> distributorProfileRespoList = profileUtility.mapDistributorProfiles(filteredProfiles, distributorPath);

        response.setStatusCode(HttpStatus.OK.value());
        response.setStatus(1);
        response.setMessageCode("MSG16");
        response.setMessage(message);
        response.setResult(distributorProfileRespoList); // filteredProfiles

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> getDistributorProfile(String phNo) {
        Optional<User> userOptional = userRepository.findByMobileNumber(phNo);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "User doesn't Exists", "MSG25"));
        }
        User user = userOptional.get();
        DistributorProfile profile = user.getDistributorProfile();

        if (profile != null) {
            String downloadUrl = profileUtility.generateDownloadUrl(distributorPath, phNo, profile.getProfilePicName(), profile.getProfilePicUrl());
            DistributorProfileRespo distributorProfile = profileUtility.mapDistributorProfile(profile, downloadUrl);
            response.setStatusCode(HttpStatus.OK.value());
            response.setStatus(1);
            response.setMessageCode("MSG17");
            response.setMessage("Distributor Profile fetched successfully");
            response.setResult(distributorProfile); // distributorProfResponse
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Distributor doesn't Exists", "MSG45"));
        }
    }

    // Delete Profile (setting profile field active as false and deleted as true)
    public ResponseEntity<?> deleteDistributorProfile(String phNo) {
        Optional<User> user = userRepository.findByMobileNumber(phNo);
        Optional<DistributorProfile> distributorProfileOptional = distributorProfileRepo.findByMobileNumber(phNo);

        if (user.isPresent()) {
            if (distributorProfileOptional.isPresent()) {
                distributorProfileOptional.get().setActive(false);
                distributorProfileOptional.get().setDeleted(true);

                distributorProfileRepo.save(distributorProfileOptional.get());

                response.setStatusCode(HttpStatus.OK.value());
                response.setStatus(1);
                response.setMessageCode("MSG1");
                response.setMessage("Distributor Profile deleted successfully");
                response.setResult(distributorProfileOptional.get());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                errorResponse.setStatus(0);
                errorResponse.setMessageCode("MSG0");
                errorResponse.setMessage("Distributor Profile with Phone number doesn't exists");
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
        } else {
            errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            errorResponse.setStatus(0);
            errorResponse.setMessageCode("MSG0");
            errorResponse.setMessage("User with Phone number Doesn't exists");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getDistributorProfilePic(String phNo) throws IOException {
        String message = "";
        Optional<DistributorProfile> distributorProfileOptional = distributorProfileRepo.findByMobileNumber(phNo);

        if (distributorProfileOptional.isPresent()) {
            DistributorProfile profile = distributorProfileOptional.get();
            if (profile.getProfilePicName() != null && profile.getProfilePicUrl() != null) {
                return s3Util.getImageFromS3Bucket(s3FolderName, profile.getProfilePicName());
            } else {
                message = "Image Not Found";
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), 0, message, "MSG26"));
            }
        } else {
            message = "Profile Picture Not Found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), 0, message, "MSG25"));
        }
    }


}