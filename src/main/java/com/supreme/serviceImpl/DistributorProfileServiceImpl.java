package com.supreme.serviceImpl;

import com.supreme.entity.*;
import com.supreme.payload.request.DistributorProfileModel;
import com.supreme.payload.response.*;
import com.supreme.repository.*;
import com.supreme.service.DistributorProfileService;
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
public class DistributorProfileServiceImpl implements DistributorProfileService {

    private final ProfileUtility profileUtility;
    private final S3Util s3Util;
    private final UserRepository userRepository;
    private final DistributorProfileRepo distributorProfileRepo;
    private final ProductRepo productRepo;
    private final DistributorProductQtyRepo distributorProductQtyRepo;
    private final ProductsNewQtyRepo productsNewQtyRepo;
    //    private final AppFeaturesRepo appFeaturesRepo;
    private final Response response;
    private final ErrorResponse errorResponse;
    private final PasswordEncoder encoder;
    @Value("${s3.distributorProfilePic}")
    private String s3FolderName;
    @Value("${path.distributor}")
    private String distributorPath;
    @Value("${path.executive}")
    private String executivePath;

    @Autowired
    public DistributorProfileServiceImpl(ProfileUtility profileUtility, S3Util s3Util, UserRepository userRepository, DistributorProfileRepo distributorProfileRepo, ProductRepo productRepo, DistributorProductQtyRepo distributorProductQtyRepo, ProductsNewQtyRepo productsNewQtyRepo, Response response, ErrorResponse errorResponse, PasswordEncoder encoder) {
        this.profileUtility = profileUtility;
        this.s3Util = s3Util;
        this.userRepository = userRepository;
        this.distributorProfileRepo = distributorProfileRepo;
        this.productRepo = productRepo;
        this.distributorProductQtyRepo = distributorProductQtyRepo;
        this.productsNewQtyRepo = productsNewQtyRepo;
        this.response = response;
        this.errorResponse = errorResponse;
        this.encoder = encoder;
    }

    // Create Distributor Profile
    @Override
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

            user.setDistributorProfile(profile);
            userRepository.save(user);

            List<DistributorProductQuantity> distributorProductQuantities = new ArrayList<>();
            for (Product product : productRepo.findAll()) {
                DistributorProductQuantity distributorProductQuantity = new DistributorProductQuantity();
                distributorProductQuantity.setDistributorProfile(profile);
                distributorProductQuantity.setProduct(product);
                distributorProductQuantity.setCurrentQty(0);
                distributorProductQuantities.add(distributorProductQuantity);
            }
            distributorProductQtyRepo.saveAll(distributorProductQuantities);

            profile.setDistributorProductQuantities(distributorProductQuantities);

            profile.setUser(user);
            distributorProfileRepo.save(profile);

            DistributorProfileRespo distributorProfile = profileUtility.mapDistributorProfile(profile, downloadUrl);
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

    // Update Distributor Profile
    @Override
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

        List<ProductsNewQty> updatedNewProducts = new ArrayList<>();
        List<DistributorProductQuantity> updatedDBProducts = new ArrayList<>();

        for (DistributorProductQuantity distProductQty : distributorProductQtyRepo.findByDistributorProfileId(profile.getId())) {
            ProductsNewQty productsNewQty = new ProductsNewQty();
            productsNewQty.setAddedDate(LocalDateTime.now());
            productsNewQty.setDistributorProfile(profile);

            switch (distProductQty.getProduct().getProductName()) {
                case "Pan Masala":
                    if (profileRequest.getPanMasalaQty() != null) {
                        // Current Qty(Total) = Current Qty + New Qty
                        distProductQty.setCurrentQty(distProductQty.getCurrentQty() + profileRequest.getPanMasalaQty());
                        productsNewQty.setProduct(distProductQty.getProduct());
                        productsNewQty.setCurrentQty(distProductQty.getCurrentQty());
                        productsNewQty.setNewQty(profileRequest.getPanMasalaQty());
                    }
                    break;
                case "Coriander Powder":
                    if (profileRequest.getCorianderQty() != null) {
                        distProductQty.setCurrentQty(distProductQty.getCurrentQty() + profileRequest.getCorianderQty());
                        productsNewQty.setProduct(distProductQty.getProduct());
                        productsNewQty.setCurrentQty(distProductQty.getCurrentQty());
                        productsNewQty.setNewQty(profileRequest.getCorianderQty());
                    }
                    break;
                case "Mutton Masala":
                    if (profileRequest.getMuttonMasalaQty() != null) {
                        distProductQty.setCurrentQty(distProductQty.getCurrentQty() + profileRequest.getMuttonMasalaQty());
                        productsNewQty.setProduct(distProductQty.getProduct());
                        productsNewQty.setCurrentQty(distProductQty.getCurrentQty());
                        productsNewQty.setNewQty(profileRequest.getMuttonMasalaQty());
                    }
                    break;
                case "Chicken Masala":
                    if (profileRequest.getChickenMasalaQty() != null) {
                        distProductQty.setCurrentQty(distProductQty.getCurrentQty() + profileRequest.getChickenMasalaQty());
                        productsNewQty.setProduct(distProductQty.getProduct());
                        productsNewQty.setCurrentQty(distProductQty.getCurrentQty());
                        productsNewQty.setNewQty(profileRequest.getChickenMasalaQty());
                    }
                    break;
                case "Chilli Powder":
                    if (profileRequest.getChilliPowderQty() != null) {
                        distProductQty.setCurrentQty(distProductQty.getCurrentQty() + profileRequest.getChilliPowderQty());
                        productsNewQty.setProduct(distProductQty.getProduct());
                        productsNewQty.setCurrentQty(distProductQty.getCurrentQty());
                        productsNewQty.setNewQty(profileRequest.getChilliPowderQty());
                    }
                    break;
                default:
                    break;
            }
            updatedDBProducts.add(distProductQty);
            updatedNewProducts.add(productsNewQty);
        }

        /*
        List<ProductDTO> updatedDBProductsList = new ArrayList<>();
        for (DistributorProductQuantity product : updatedDBProducts){
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductId(product.getId());
            productDTO.setProductName(product.getProduct().getProductName());
            productDTO.setProductImgName(product.getProduct().getProductImgName());
            productDTO.setProductImgUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path(product.getProduct().getProductImgUrl()).toUriString());
            productDTO.setCurrentQuantity(product.getCurrentQty());
            updatedDBProductsList.add(productDTO);
        }
         */

        List<ProductDTO> updatedDBProductsList = profileUtility.mapDistributorProductQuantityToProductDTO(updatedDBProducts);

        userRepository.save(user);
        distributorProfileRepo.save(profile);
        distributorProductQtyRepo.saveAll(updatedDBProducts);
        productsNewQtyRepo.saveAll(updatedNewProducts);

        DistributorProfileRespo distributorProfile = profileUtility.mapDistributorProfileDB(profile, updatedDBProductsList, downloadUrl);
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setStatus(1);
        response.setMessageCode("MSG1");
        response.setMessage("Distributor Profile Updated Successfully");
        response.setResult(distributorProfile);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Fetch List of Executives corresponding to Distributor
    @Override
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

    // Fetch Distributor Profile by status
    @Override
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

    // Fetch Distributor Profile Image
    @Override
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

    // Delete Distributor Profile (set profile field active as false and deleted as true)
    @Override
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

    // Fetch Distributor Profile Image
    @Override
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