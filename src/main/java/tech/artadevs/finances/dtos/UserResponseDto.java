package tech.artadevs.finances.dtos;

import java.util.Date;

public class UserResponseDto {
	private Long id;
	private String name;
	private Integer age;
	private String email;
	private Long accountNumber;
	private Date createdAt;

	public UserResponseDto() {
	}

	public String getName() {
		return name;
	}

	public UserResponseDto setName(String name) {
		this.name = name;
		return this;
	}

	public Integer getAge() {
		return age;
	}

	public UserResponseDto setAge(Integer age) {
		this.age = age;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public UserResponseDto setEmail(String email) {
		this.email = email;
		return this;
	}

	public Long getAccountNumber() {
		return accountNumber;
	}

	public UserResponseDto setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
		return this;
	}

	public Long getId() {
		return id;
	}

	public UserResponseDto setId(Long id) {
		this.id = id;
		return this;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public UserResponseDto setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
	}

}