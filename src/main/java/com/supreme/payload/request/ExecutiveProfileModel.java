package com.supreme.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExecutiveProfileModel {

    private Long distributorId;

    private MultipartFile profilePic;

    @NotNull(message = "First Name must not be null")
    @Size(min = 3, max = 20, message = "First Name must be between 5 and 30 characters long")
    @Pattern(regexp = "^[a-zA-Z]*$", message = "First Name must not contain numbers or special characters")
    private String firstName;

    @NotNull(message = "Last Name must not be null")
    @Size(min = 3, max = 20, message = "Last Name must be between 5 and 30 characters long")
    @Pattern(regexp = "^[a-zA-Z]*$", message = "Last Name must not contain numbers or special characters")
    private String lastName;

    //	private Role role;
    @NotNull(message = "Mobile Number must not be null")
    @Size(min = 10, max = 10, message = "Mobile Number must be exactly 10 digits long")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile Number must contain only Numbers") // "^\\d{10}$"
    private String mobileNumber;

    @NotNull(message = "Pin shouldn't be null")
    @Pattern(regexp = "^\\d{6}$", message = "invalid Pin entered")
    private String pin;

    @NotNull(message = "Status of staff must not be null")
    private Boolean active;
}
