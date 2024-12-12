package tech.artadevs.finances.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import tech.artadevs.finances.dtos.FinancialTransactionRequestDto;
import tech.artadevs.finances.dtos.FinancialTransactionResponseDto;
import tech.artadevs.finances.services.FinancialTransactionService;

@Validated
@RestController
@RequestMapping("/user/me/transactions")
public class FinancialTransactionController {

    @Autowired
    private FinancialTransactionService financialTransactionService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public FinancialTransactionResponseDto createTransaction(
            @Valid @RequestBody FinancialTransactionRequestDto financialTransactionRequest) {
        return financialTransactionService.create(financialTransactionRequest);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public void updateTransaction(@PathVariable Long id,
            @Valid @RequestBody FinancialTransactionRequestDto financialTransactionRequest) {
        financialTransactionService.update(id, financialTransactionRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public FinancialTransactionResponseDto getUserTransactionById(@PathVariable Long id) {
        return financialTransactionService.getOwnById(id);
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public List<FinancialTransactionResponseDto> listAllTransactions() {
        return financialTransactionService.listAllForCurrentUser();
    }

    @GetMapping("/total")
    @SecurityRequirement(name = "bearerAuth")
    public Double calculateTotalTransactionsValue() {
        return financialTransactionService.calculateTotalTransactionValueForCurrentUser();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public void deleteOwnItemById(@PathVariable Long id) {
        financialTransactionService.deleteOwnItemById(id);
    }
}
