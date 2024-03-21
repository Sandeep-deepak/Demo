package com.supreme.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class DistributorProfileModel {

    private MultipartFile profilePic;

    @NotNull(message = "First Name must not be null")
    @Size(min = 3, max = 20, message = "First Name must be between 5 and 30 characters long")
    @Pattern(regexp = "^[a-zA-Z]*$", message = "First Name must not contain numbers or special characters")
    private String firstName;

    @NotNull(message = "Last Name must not be null")
    @Size(min = 3, max = 20, message = "Last Name must be between 5 and 30 characters long")
    @Pattern(regexp = "^[a-zA-Z]*$", message = "Last Name must not contain numbers or special characters")
    private String lastName;

    @NotNull(message = "Mobile Number must not be null")
    @Size(min = 10, max = 10, message = "Mobile Number must be exactly 10 digits long")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile Number must contain only Numbers")
    private String mobileNumber;

    @NotNull(message = "Pin shouldn't be null")
    @Pattern(regexp = "^\\d{6}$", message = "invalid Pin entered")
    private String pin;

    @NotNull(message = "Status of staff must not be null")
    private Boolean active;

    private Integer panMasalaQty;
    private Integer corianderQty;
    private Integer muttonMasalaQty;
    private Integer chickenMasalaQty;
    private Integer chilliPowderQty;
}
