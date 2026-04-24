package tech.artadevs.finances.dtos;

import jakarta.validation.constraints.NotBlank;

public class UserLoginRequestDto {

	@NotBlank(message = "The email address is required.")
	private String email;

	@NotBlank(message = "The password is required.")
	private String password;

	public UserLoginRequestDto() {
	}

	public String getEmail() {
		return email;
	}

	public UserLoginRequestDto setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public UserLoginRequestDto setPassword(String password) {
		this.password = password;
		return this;
	}
}