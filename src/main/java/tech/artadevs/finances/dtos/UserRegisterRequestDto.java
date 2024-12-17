package tech.artadevs.finances.dtos;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern.Flag;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterRequestDto {
	@SuppressWarnings("deprecation")
	@NotEmpty(message = "The full name is required.")
	@Size(min = 2, max = 100, message = "The length of full name must be between 2 and 100 characters.")
	private String name;

	@Min(value = 18, message = "Age must be at least 18.")
	@Max(value = 120, message = "Age must not exceed 120.")
	private Integer age;

	@SuppressWarnings("deprecation")
	@NotEmpty(message = "The email address is required.")
	@Email(message = "The email address is invalid.", flags = { Flag.CASE_INSENSITIVE })
	private String email;

	@NotNull(message = "The account number is required.")
	@Min(value = 1, message = "Account number should be greater than zero.")
	private Long accountNumber;

	@SuppressWarnings("deprecation")
	@NotEmpty(message = "The password is required.")
	@Size(min = 8, max = 100, message = "The length of password must be between 8 and 100 characters.")
	private String password;

	public UserRegisterRequestDto() {
	}

	public String getName() {
		return name;
	}

	public UserRegisterRequestDto setName(String name) {
		this.name = name;
		return this;
	}

	public Integer getAge() {
		return age;
	}

	public UserRegisterRequestDto setAge(Integer age) {
		this.age = age;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public UserRegisterRequestDto setEmail(String email) {
		this.email = email;
		return this;
	}

	public Long getAccountNumber() {
		return accountNumber;
	}

	public UserRegisterRequestDto setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public UserRegisterRequestDto setPassword(String password) {
		this.password = password;
		return this;
	}
}