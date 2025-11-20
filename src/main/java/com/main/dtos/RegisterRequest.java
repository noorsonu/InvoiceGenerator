package com.main.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

	@NotBlank(message = "Email must not be blank")
	@Email(message = "Email must be a valid email")
	private String email;

	@NotBlank(message = "Phone no must not be blank")
	@Pattern(
		regexp = "^[6-9]\\d{9}$",
		message = "Phone number must be 10 digits and start with 6, 7, 8, or 9"
	)
	private String phoneNumber;

	@NotBlank(message = "Name no must not be blank")
	private String name;

	@NotBlank(message = "Password must not be blank")
	@Size(min = 8, message = "Password must be at least 8 characters")
	@Pattern(
		regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
		message = "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character"
	)
	@Schema(
		description = "Password must be at least 8 characters and include uppercase, lowercase, number, and special character",
		example = "Str0ng@Pass"
	)
	private String password;

}
