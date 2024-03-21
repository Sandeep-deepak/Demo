package com.supreme.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotNull(message = "Mobile number shouldn't be null")
    @Pattern(regexp = "^\\d{10}$", message = "invalid mobile number entered")
    private String mobileNumber;

    @NotNull(message = "Pin shouldn't be null")
    @Pattern(regexp = "^\\d{6}$", message = "invalid Pin entered")
    private String pin;

}
