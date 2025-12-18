package com.org.dto;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class UserLoginRequest {
	@NotBlank(message = "Email is required")
    @Email(message = "invalid email format")
	@Size(max = 150)
	private String emailId;

	@NotBlank(message = "password is required")
	private String password;

}
