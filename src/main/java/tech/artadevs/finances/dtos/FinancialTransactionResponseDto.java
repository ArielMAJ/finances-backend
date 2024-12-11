package tech.artadevs.finances.dtos;

import lombok.Data;

@Data
public class FinancialTransactionResponseDto {

	private Long id;
	private Double value;
	private String description;

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
}