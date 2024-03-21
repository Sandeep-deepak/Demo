package com.supreme.exception;

import lombok.*;
import org.springframework.http.HttpStatusCode;

//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CustomErrorResponseException extends RuntimeException {
	private HttpStatusCode statusCode;
    private int status;
    private String message;
    private String messageCode;
}
