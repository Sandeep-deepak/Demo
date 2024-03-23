package com.supreme.payload.response;

import com.supreme.entity.ERole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ExecutiveProfileResponse {

    private Long distributorId;
    private String distributorName;
    private Long executiveId;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String pin;
    private ERole role;
    private boolean active;
    private boolean deleted;
    private String profilePicName;
    private String profilePicUrl;
}
