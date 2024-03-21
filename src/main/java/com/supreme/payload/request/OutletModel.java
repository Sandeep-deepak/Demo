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
public class OutletModel {
    @NotNull(message = "Outlet Name must not be null")
    @Size(min = 3, max = 20, message = "Outlet Name must be between 5 and 30 characters long")
    @Pattern(regexp = "^[a-zA-Z]*$", message = "Outlet Name must not contain numbers or special characters")
    private String outletName;
    @NotNull(message = "Mobile Number must not be null")
    @Size(min = 10, max = 10, message = "Mobile Number must be exactly 10 digits long")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile Number must contain only Numbers")
    private String mobileNumber;
    @NotNull(message = "Outlet Address must not be null")
    @Size(min = 3, max = 20, message = "Outlet Address should contain atleast 10 characters")
    private String outletAddress;
    private MultipartFile outletImage;
}
