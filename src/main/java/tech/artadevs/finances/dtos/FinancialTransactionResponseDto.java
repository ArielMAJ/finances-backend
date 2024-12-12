package tech.artadevs.finances.dtos;

import java.util.Date;

import lombok.Data;

@Data
public class FinancialTransactionResponseDto {

	private Long id;
	private Double value;
	private String description;
	private Date createdAt;
	private Date updatedAt;

	public FinancialTransactionResponseDto() {
	}

	public Long getId() {
		return id;
	}

	public Double getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	public Date getCreatedAt() {
		return createdAt;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}

	public FinancialTransactionResponseDto setId(Long id) {
		this.id = id;
		return this;
	}

	public FinancialTransactionResponseDto setValue(Double value) {
		this.value = value;
		return this;
	}

	public FinancialTransactionResponseDto setDescription(String description) {
		this.description = description;
		return this;
	}

	public FinancialTransactionResponseDto setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public FinancialTransactionResponseDto setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}
}