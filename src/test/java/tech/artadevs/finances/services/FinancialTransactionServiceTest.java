package tech.artadevs.finances.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tech.artadevs.finances.dtos.FinancialTransactionRequestDto;
import tech.artadevs.finances.dtos.FinancialTransactionResponseDto;
import tech.artadevs.finances.exception.ResourceNotFoundException;
import tech.artadevs.finances.models.FinancialTransaction;
import tech.artadevs.finances.models.User;
import tech.artadevs.finances.repositories.FinancialTransactionRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinancialTransactionServiceTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private FinancialTransactionRepository financialTransactionRepository;

    @InjectMocks
    private FinancialTransactionService financialTransactionService;

    private User mockUser;
    private FinancialTransaction mockTransaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");

        mockTransaction = new FinancialTransaction();
        mockTransaction.setId(1L);
        mockTransaction.setValue(100.0);
        mockTransaction.setDescription("Test transaction");
        mockTransaction.setUser(mockUser);
    }

    @Test
    void testCreate() {
        FinancialTransactionRequestDto requestDto = new FinancialTransactionRequestDto();
        requestDto.setValue(200.0);
        requestDto.setDescription("New transaction");

        when(authenticationService.getCurrentUser()).thenReturn(mockUser);
        when(financialTransactionRepository.save(any(FinancialTransaction.class))).thenReturn(mockTransaction);

        FinancialTransactionResponseDto responseDto = financialTransactionService.create(requestDto);

        assertNotNull(responseDto);
        assertEquals(mockTransaction.getId(), responseDto.getId());
        verify(financialTransactionRepository, times(1)).save(any(FinancialTransaction.class));
    }

    @Test
    void testUpdate_Success() {
        FinancialTransactionRequestDto requestDto = new FinancialTransactionRequestDto();
        requestDto.setValue(300.0);
        requestDto.setDescription("Updated transaction");

        when(authenticationService.getCurrentUser()).thenReturn(mockUser);
        when(financialTransactionRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.of(mockTransaction));

        financialTransactionService.update(1L, requestDto);

        verify(financialTransactionRepository, times(1)).save(mockTransaction);
        assertEquals(300.0, mockTransaction.getValue());
        assertEquals("Updated transaction", mockTransaction.getDescription());
    }

    @Test
    void testUpdate_ResourceNotFound() {
        FinancialTransactionRequestDto requestDto = new FinancialTransactionRequestDto();

        when(authenticationService.getCurrentUser()).thenReturn(mockUser);
        when(financialTransactionRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> financialTransactionService.update(1L, requestDto));
        verify(financialTransactionRepository, never()).save(any(FinancialTransaction.class));
    }

    @Test
    void testListAllForCurrentUser() {
        when(authenticationService.getCurrentUser()).thenReturn(mockUser);
        when(financialTransactionRepository.findByUser(mockUser)).thenReturn(List.of(mockTransaction));

        List<FinancialTransactionResponseDto> transactions = financialTransactionService.listAllForCurrentUser();

        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        assertEquals(mockTransaction.getId(), transactions.get(0).getId());
    }

    @Test
    void testGetCurrentUserTransactionsTotalValue() {
        when(authenticationService.getCurrentUser()).thenReturn(mockUser);
        when(financialTransactionRepository.getUserTransactionsTotalValue(mockUser)).thenReturn(500.0);

        Double totalValue = financialTransactionService.getCurrentUserTransactionsTotalValue();

        assertNotNull(totalValue);
        assertEquals(500.0, totalValue);
    }

    @Test
    void testGetOwnById_Success() {
        when(authenticationService.getCurrentUser()).thenReturn(mockUser);
        when(financialTransactionRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.of(mockTransaction));

        FinancialTransactionResponseDto responseDto = financialTransactionService.getOwnById(1L);

        assertNotNull(responseDto);
        assertEquals(mockTransaction.getId(), responseDto.getId());
    }

    @Test
    void testGetOwnById_ResourceNotFound() {
        when(authenticationService.getCurrentUser()).thenReturn(mockUser);
        when(financialTransactionRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> financialTransactionService.getOwnById(1L));
    }

    @Test
    void testDeleteOwnItemById_Success() {
        when(authenticationService.getCurrentUser()).thenReturn(mockUser);
        when(financialTransactionRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.of(mockTransaction));

        financialTransactionService.deleteOwnItemById(1L);

        verify(financialTransactionRepository, times(1)).save(mockTransaction);
        assertNotNull(mockTransaction.getDeletedAt());
    }

    @Test
    void testDeleteOwnItemById_ResourceNotFound() {
        when(authenticationService.getCurrentUser()).thenReturn(mockUser);
        when(financialTransactionRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> financialTransactionService.deleteOwnItemById(1L));
    }
}
