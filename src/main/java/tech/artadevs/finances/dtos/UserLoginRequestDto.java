package tech.artadevs.finances.dtos;

public class UserLoginRequestDto {
	private String email;
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