
package tech.artadevs.finances.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.artadevs.finances.controller.UserController;
import tech.artadevs.finances.dtos.FinancialTransactionRequestDto;
import tech.artadevs.finances.dtos.FinancialTransactionResponseDto;
import tech.artadevs.finances.exception.ResourceNotFoundException;
import tech.artadevs.finances.models.FinancialTransaction;
import tech.artadevs.finances.models.User;
import tech.artadevs.finances.repositories.FinancialTransactionRepository;

@Service
public class FinancialTransactionService {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private FinancialTransactionRepository financialTransactionRepository;

    public FinancialTransactionResponseDto create(FinancialTransactionRequestDto financialTransactionRequest) {
        User currentUser = authenticationService.getCurrentUser();
        logger.info("New financial transaction creation request description={} from user={}",
                financialTransactionRequest.getDescription(), currentUser.getEmail());

        FinancialTransaction newFinancialTransaction = new FinancialTransaction()
                .setValue(financialTransactionRequest.getValue())
                .setDescription(financialTransactionRequest.getDescription())
                .setUser(currentUser);

        newFinancialTransaction = financialTransactionRepository.save(newFinancialTransaction);
        logger.info("New financial transaction, with id={}, created for user={},", newFinancialTransaction.getId());
        return newFinancialTransaction.toFinancialTransactionResponseDto();
    }

    public List<FinancialTransactionResponseDto> listAllForCurrentUser() {
        User currentUser = authenticationService.getCurrentUser();
        logger.info("Fetching all financial transactions for user={}", currentUser.getEmail());
        return financialTransactionRepository.findByUser(currentUser)
                .stream()
                .map(FinancialTransaction::toFinancialTransactionResponseDto)
                .toList();
    }

    public double calculateTotalTransactionValueForCurrentUser() {
        User currentUser = authenticationService.getCurrentUser();
        logger.info("Calculating total financial transaction value for user={}", currentUser.getEmail());
        List<FinancialTransaction> transactions = financialTransactionRepository.findByUser(currentUser);
        return transactions
                .stream()
                .mapToDouble(FinancialTransaction::getValue)
                .sum();
    }

    public void deleteOwnItemById(Long id) {
        User currentUser = authenticationService.getCurrentUser();
        Optional<FinancialTransaction> optFinancialTransaction = financialTransactionRepository.findByIdAndUser(id,
                currentUser);
        if (optFinancialTransaction.isEmpty())
            throw new ResourceNotFoundException("Financial Transaction");

        FinancialTransaction financialTransaction = optFinancialTransaction.get();

        financialTransaction.setDeletedAt(new Date());
        financialTransactionRepository.save(financialTransaction);
    }
}
