package com.supreme.exception;

import lombok.*;
import org.springframework.http.HttpStatusCode;

//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ErrorRespException extends RuntimeException {
	private HttpStatusCode statusCode;
    private String message;
}
