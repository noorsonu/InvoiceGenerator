package com.main.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ErrorResponse", description = "Standard error response body")
public class ErrorResponse {

    @Schema(description = "Human-readable error message", example = "Email already in use")
    private String message;


    public static ErrorResponse of(org.springframework.http.HttpStatus status, String message, String string) {
        return ErrorResponse.builder()
                .message(message)
                .build();
    }
}
