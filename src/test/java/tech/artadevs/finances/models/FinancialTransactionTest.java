package tech.artadevs.finances.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.artadevs.finances.dtos.FinancialTransactionResponseDto;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class FinancialTransactionTest {

    private FinancialTransaction financialTransaction;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User()
                .setId(1L)
                .setName("Test User")
                .setEmail("test@example.com")
                .setPassword("password")
                .setEnabled(true);

        financialTransaction = new FinancialTransaction()
                .setId(1L)
                .setValue(100.0)
                .setDescription("Test Transaction")
                .setUser(user)
                .setCreatedAt(new Date())
                .setUpdatedAt(new Date());
    }

    @Test
    void testConstructor() {
        FinancialTransaction transaction = new FinancialTransaction(200.0, "Constructor Transaction", user);
        assertEquals(200.0, transaction.getValue());
        assertEquals("Constructor Transaction", transaction.getDescription());
        assertEquals(user, transaction.getUser());
        assertNull(transaction.getDeletedAt());
    }

    @Test
    void testSetCreatedAt() {
        Date newDate = new Date();
        financialTransaction.setCreatedAt(newDate);
        assertEquals(newDate, financialTransaction.getCreatedAt());
    }

    @Test
    void testSetUpdatedAt() {
        Date newDate = new Date();
        financialTransaction.setUpdatedAt(newDate);
        assertEquals(newDate, financialTransaction.getUpdatedAt());
    }

    @Test
    void testGetUser() {
        assertEquals(user, financialTransaction.getUser());
    }

    @Test
    void testGetCreatedAt() {
        Date createdAt = financialTransaction.getCreatedAt();
        assertNotNull(createdAt);
    }

    @Test
    void testGetUpdatedAt() {
        Date updatedAt = financialTransaction.getUpdatedAt();
        assertNotNull(updatedAt);
    }

    @Test
    void testSetDeletedAt() {
        Date deletedAt = new Date();
        financialTransaction.setDeletedAt(deletedAt);
        assertEquals(deletedAt, financialTransaction.getDeletedAt());
    }

    @Test
    void testToFinancialTransactionResponseDto() {
        FinancialTransactionResponseDto dto = financialTransaction.toFinancialTransactionResponseDto();

        assertNotNull(dto);
        assertEquals(financialTransaction.getId(), dto.getId());
        assertEquals(financialTransaction.getValue(), dto.getValue());
        assertEquals(financialTransaction.getDescription(), dto.getDescription());
        assertEquals(financialTransaction.getCreatedAt(), dto.getCreatedAt());
        assertEquals(financialTransaction.getUpdatedAt(), dto.getUpdatedAt());
    }
}
