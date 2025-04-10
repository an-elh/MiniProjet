package com.mycompany.miniprojet.testController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dao.*;
import entite.*;
import controller.*;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

    @Mock
    private TransactionDAO transactionDAO;

    @InjectMocks
    private TransactionController transactionController;

    private Transaction testTransaction;
    private final String testAccountNumber = "ACC123456";

    @BeforeEach
    void setUp() {
        testTransaction = new Transaction(
            "DEPOSIT",
            BigDecimal.valueOf(1000),
            new Date(System.currentTimeMillis()),
            testAccountNumber
        );
    }

    @Test
    void createTransaction_Success() throws SQLException {
        // Arrange
        when(transactionDAO.createTransaction(any(Transaction.class))).thenReturn(true);

        // Act
        boolean result = transactionController.createTransaction(
            "DEPOSIT",
            BigDecimal.valueOf(1000),
            new Date(System.currentTimeMillis()),
            testAccountNumber
        );

        // Assert
        assertTrue(result);
        
        // Verify DAO interaction
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionDAO).createTransaction(transactionCaptor.capture());
        
        Transaction captured = transactionCaptor.getValue();
        assertEquals("DEPOSIT", captured.getType());
        assertEquals(BigDecimal.valueOf(1000), captured.getMontant());
        assertEquals(testAccountNumber, captured.getNumCompte());
    }

    @Test
    void createTransaction_Failure() throws SQLException {
        // Arrange
        when(transactionDAO.createTransaction(any(Transaction.class)))
            .thenThrow(new SQLException("Database error"));

        // Act
        boolean result = transactionController.createTransaction(
            "DEPOSIT",
            BigDecimal.valueOf(1000),
            new Date(System.currentTimeMillis()),
            testAccountNumber
        );

        // Assert
        assertFalse(result);
    }

    @Test
    void getTransactions_Success() throws SQLException {
        // Arrange
        List<Transaction> expected = Arrays.asList(testTransaction);
        when(transactionDAO.getTransactions(testAccountNumber)).thenReturn(expected);

        // Act
        List<Transaction> result = transactionController.getTransactions(testAccountNumber);

        // Assert
        assertEquals(1, result.size());
        assertEquals(testTransaction, result.get(0));
    }

    @Test
    void getTransactions_Empty() throws SQLException {
        // Arrange
        when(transactionDAO.getTransactions(testAccountNumber)).thenReturn(List.of());

        // Act
        List<Transaction> result = transactionController.getTransactions(testAccountNumber);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getTransactions_Error() throws SQLException {
        // Arrange
        when(transactionDAO.getTransactions(testAccountNumber))
            .thenThrow(new SQLException("Connection failed"));

        // Act
        List<Transaction> result = transactionController.getTransactions(testAccountNumber);

        // Assert
        assertNull(result);
    }

    @Test
    void createTransaction_InvalidParameters() {
        // Act & Assert
        assertFalse(transactionController.createTransaction(
            null, 
            BigDecimal.ZERO, 
            new Date(System.currentTimeMillis()), 
            testAccountNumber
        ));

        assertFalse(transactionController.createTransaction(
            "WITHDRAWAL", 
            BigDecimal.valueOf(-100), 
            new Date(System.currentTimeMillis()), 
            testAccountNumber
        ));
    }
}