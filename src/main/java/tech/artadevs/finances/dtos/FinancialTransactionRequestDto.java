package tech.artadevs.finances.dtos;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FinancialTransactionRequestDto {

	private Double value;

	@Size(max = 100, message = "The length of description must be at most 100 characters.")
	private String description;

	public FinancialTransactionRequestDto() {
	}

	public Double getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	public FinancialTransactionRequestDto setValue(Double value) {
		this.value = value;
		return this;
	}

	public FinancialTransactionRequestDto setDescription(String description) {
		this.description = description;
		return this;
	}
}