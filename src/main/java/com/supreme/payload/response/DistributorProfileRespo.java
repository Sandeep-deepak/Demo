package com.supreme.payload.response;

import com.supreme.entity.ERole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class DistributorProfileRespo {

    private Long distributorId;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String pin;
    private ERole role;
    private boolean active;
    private boolean deleted;
    private String profilePicName;
    private String profilePicUrl;

    private List<?> products; // ProductDTO
    private List<ExecutiveProfileResponse> executiveProfiles;

}
