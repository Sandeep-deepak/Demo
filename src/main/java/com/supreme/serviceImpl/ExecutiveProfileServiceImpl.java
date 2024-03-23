package com.supreme.serviceImpl;

import com.supreme.entity.DistributorProfile;
import com.supreme.entity.ERole;
import com.supreme.entity.ExecutiveProfile;
import com.supreme.entity.User;
import com.supreme.payload.request.ExecutiveProfileModel;
import com.supreme.payload.response.ErrorResponse;
import com.supreme.payload.response.ExecutiveProfileResponse;
import com.supreme.payload.response.Response;
import com.supreme.repository.DistributorProfileRepo;
import com.supreme.repository.ExecutiveProfileRepo;
import com.supreme.repository.UserRepository;
import com.supreme.service.ExecutiveProfileService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExecutiveProfileServiceImpl implements ExecutiveProfileService {

    private final ProfileUtility profileUtility;
    private final S3Util s3Util;
    private final UserRepository userRepository;
    private final ExecutiveProfileRepo executiveProfileRepo;
    private final DistributorProfileRepo distributorProfileRepo;
    //    private final AppFeaturesRepo appFeaturesRepo;
    private final Response response;
    private final ErrorResponse errorResponse;
    private final PasswordEncoder encoder;
    @Value("${s3.executiveProfilePic}")
    private String s3FolderName;
    @Value("${path.executive}")
    private String executivePath;

    @Autowired
    public ExecutiveProfileServiceImpl(ProfileUtility profileUtility, S3Util s3Util, UserRepository userRepository, ExecutiveProfileRepo executiveProfileRepo, DistributorProfileRepo distributorProfileRepo, Response response, ErrorResponse errorResponse, PasswordEncoder encoder) {
        this.profileUtility = profileUtility;
        this.s3Util = s3Util;
        this.userRepository = userRepository;
        this.executiveProfileRepo = executiveProfileRepo;
        this.distributorProfileRepo = distributorProfileRepo;
        this.response = response;
        this.errorResponse = errorResponse;
        this.encoder = encoder;
    }

    // Create Executive Profile
    @Override
    public ResponseEntity<?> addExecutiveProfile(ExecutiveProfileModel profileRequest) {
//        Optional<AppFeatures> appFeatures = appFeaturesRepo.findById(1L);
        Optional<User> userOptional = userRepository.findByMobileNumber(profileRequest.getMobileNumber());
        Optional<DistributorProfile> distributorProfileOptional = distributorProfileRepo.findById(profileRequest.getDistributorId());
        Optional<ExecutiveProfile> executiveProfileOptional = executiveProfileRepo.findByMobileNumber(profileRequest.getMobileNumber());

        if (distributorProfileOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Distributor Profile with id doesn't exists", "MSG25"));
        }
        DistributorProfile distributorProfile = distributorProfileOptional.get();

        if (userOptional.isEmpty() && executiveProfileOptional.isEmpty()) {
            User user = new User(profileRequest.getMobileNumber(),
                    encoder.encode(profileRequest.getPin()), ERole.EXECUTIVE);

            ExecutiveProfile profile = new ExecutiveProfile();
            profile.setDistributorProfile(distributorProfile);
            profile.setFirstName(profileRequest.getFirstName());
            profile.setLastName(profileRequest.getLastName());
            profile.setMobileNumber(profileRequest.getMobileNumber());
            profile.setActive(profileRequest.getActive());
            profile.setDeleted(false);

            String downloadUrl = null;
            String downloadUri = executivePath + profileRequest.getMobileNumber();

            MultipartFile file = profileRequest.getProfilePic();
            if (file != null && !file.isEmpty()) {
                String s3Url = s3Util.uploadFile(s3FolderName, file);
                String fileName = s3Url.substring(s3Url.lastIndexOf("/") + 1);
                profile.setProfilePicName(fileName);
                profile.setProfilePicUrl(downloadUri);
                downloadUrl = profileUtility.generateDownloadUrl(executivePath, profileRequest.getMobileNumber());
            }

            user.setExecutiveProfile(profile);
            userRepository.save(user);
            profile.setUser(user);
            executiveProfileRepo.save(profile);

            ExecutiveProfileResponse executiveProfile = profileUtility.mapExecutiveProfile(profile, downloadUrl);
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setStatus(1);
            response.setMessageCode("MSG1");
            response.setMessage("Executive Profile added successfully");
            response.setResult(executiveProfile);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Phone number already exists", "MSG25"));
        }
    }

    // Update Executive Profile
    @Override
    public ResponseEntity<?> updateExecutiveProfile(String phNo, ExecutiveProfileModel profileRequest) {
        Optional<User> userOptional = userRepository.findByMobileNumber(phNo);
//        Optional<AppFeatures> appFeatures = appFeaturesRepo.findById(1L);
        Optional<ExecutiveProfile> executiveProfileOptional = executiveProfileRepo.findByMobileNumber(phNo);

        if (Boolean.TRUE.equals(userRepository.existsByMobileNumber(profileRequest.getMobileNumber()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Mobile number already exists", "MSG25"));
        }
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "User doesn't Exists", "MSG25"));
        }
        User user = userOptional.get();

        if (executiveProfileOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Executive doesn't Exists", "MSG25"));
        }
        ExecutiveProfile profile = executiveProfileOptional.get();

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

        String downloadUri = executivePath + profileRequest.getMobileNumber();
        String downloadUrl = profileUtility.generateDownloadUrl(executivePath, profile.getMobileNumber(), profile.getProfilePicName(), profile.getProfilePicUrl());

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
            downloadUrl = profileUtility.generateDownloadUrl(executivePath, profileRequest.getMobileNumber());
        }

        userRepository.save(user);
        executiveProfileRepo.save(profile);

        ExecutiveProfileResponse executiveProfile = profileUtility.mapExecutiveProfile(profile, downloadUrl);
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setStatus(1);
        response.setMessageCode("MSG1");
        response.setMessage("Executive Profile Updated Successfully");
        response.setResult(executiveProfile);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Fetch Executive Profile by status
    @Override
    public ResponseEntity<?> getExecutiveProfilesByStatus(Boolean active, Boolean deleted) {
        // Fetch all profiles when active and deleted parameters are not passed
        if (active == null && deleted == null) {
            List<ExecutiveProfile> profileList = executiveProfileRepo.findAll();
            List<ExecutiveProfileResponse> executiveProfileResponseList = profileUtility.mapExecutiveProfiles(profileList, executivePath);

            response.setStatusCode(HttpStatus.OK.value());
            response.setStatus(1);
            response.setMessageCode("MSG16");
            response.setMessage("Executive Profile list fetched successfully");
            response.setResult(executiveProfileResponseList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // Fetch profiles based on active and deleted status if provided
        List<ExecutiveProfile> filteredProfiles = new ArrayList<>();
        if (active != null && deleted != null) {
            filteredProfiles = executiveProfileRepo.findByActiveAndDeleted(active, deleted);
        } else if (active != null) {
            filteredProfiles = executiveProfileRepo.getExecutiveProfileByActive(active);
        } else if (deleted != null) {
            filteredProfiles = executiveProfileRepo.getExecutiveProfileByDeleted(deleted);
        }

        String message;
        if (active != null && !active) {
            message = "Inactive Executive Profile list fetched successfully";
        } else if (deleted != null && deleted.equals(Boolean.TRUE)) {
            message = "Deleted Executive Profile list fetched successfully";
        } else if (active != null) { // && active
            message = "Active Executive Profile list fetched successfully";
        } else {
            message = "Executive Profile list fetched successfully";
        }

        List<ExecutiveProfileResponse> executiveProfileResponseList = profileUtility.mapExecutiveProfiles(filteredProfiles, executivePath);
        response.setStatusCode(HttpStatus.OK.value());
        response.setStatus(1);
        response.setMessageCode("MSG16");
        response.setMessage(message);
        response.setResult(executiveProfileResponseList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Fetch Executive Profile by mobile number
    @Override
    public ResponseEntity<?> getExecutiveProfile(String phNo) {
        Optional<User> userOptional = userRepository.findByMobileNumber(phNo);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "User doesn't Exists", "MSG25"));
        }
        User user = userOptional.get();
        ExecutiveProfile profile = user.getExecutiveProfile();

        if (profile != null) {
            String downloadUrl = profileUtility.generateDownloadUrl(executivePath, phNo, profile.getProfilePicName(), profile.getProfilePicUrl());
            ExecutiveProfileResponse executiveProfile = profileUtility.mapExecutiveProfile(profile, downloadUrl);

            response.setStatusCode(HttpStatus.OK.value());
            response.setStatus(1);
            response.setMessageCode("MSG17");
            response.setMessage("Executive Profile fetched successfully");
            response.setResult(executiveProfile);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 0, "Executive doesn't Exists", "MSG45"));
        }
    }

    // Delete Profile (set profile field active as false and deleted as true)
    @Override
    public ResponseEntity<?> deleteExecutiveProfile(String phNo) {
        Optional<User> user = userRepository.findByMobileNumber(phNo);
        Optional<ExecutiveProfile> executiveProfileOptional = executiveProfileRepo.findByMobileNumber(phNo);

        if (user.isPresent()) {
            if (executiveProfileOptional.isPresent()) {
                executiveProfileOptional.get().setActive(false);
                executiveProfileOptional.get().setDeleted(true);

                executiveProfileRepo.save(executiveProfileOptional.get());

                response.setStatusCode(HttpStatus.OK.value());
                response.setStatus(1);
                response.setMessageCode("MSG1");
                response.setMessage("Executive Profile deleted successfully");
                response.setResult(executiveProfileOptional.get());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                errorResponse.setStatus(0);
                errorResponse.setMessageCode("MSG0");
                errorResponse.setMessage("Executive Profile with Phone number doesn't exists");
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

    // Fetch Executive Profile Image
    @Override
    public ResponseEntity<?> getExecutiveProfilePic(String phNo) throws IOException {
        String message = "";
        Optional<ExecutiveProfile> executiveProfileOptional = executiveProfileRepo.findByMobileNumber(phNo);

        if (executiveProfileOptional.isPresent()) {
            ExecutiveProfile profile = executiveProfileOptional.get();
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
