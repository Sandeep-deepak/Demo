package com.supreme.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ErrorResponse {

    private int statusCode;
    private int status;
    private String message;
    private String messageCode;

}
