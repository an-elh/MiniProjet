package com.mycompany.miniprojet.testEntite;

import entite.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionTest {

    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        transaction = new Transaction("Deposit", new BigDecimal("500.00"), new Date(), "12345");
    }

    @Test
    public void testGetTransactionId_ShouldReturnTransactionId() {
        transaction.setTransactionId(1);
        assertEquals(1, transaction.getTransactionId(), "The transaction ID should be 1.");
    }

    @Test
    public void testGetType_ShouldReturnType() {
        assertEquals("Deposit", transaction.getType(), "The transaction type should be 'Deposit'.");
    }

    @Test
    public void testSetType_ShouldSetTypeCorrectly() {
        transaction.setType("Withdrawal");
        assertEquals("Withdrawal", transaction.getType(), "The transaction type should be updated to 'Withdrawal'.");
    }

    @Test
    public void testGetMontant_ShouldReturnMontant() {
        assertEquals(new BigDecimal("500.00"), transaction.getMontant(), "The amount should be '500.00'.");
    }

    @Test
    public void testSetMontant_ShouldSetMontantCorrectly() {
        transaction.setMontant(new BigDecimal("1000.00"));
        assertEquals(new BigDecimal("1000.00"), transaction.getMontant(), "The amount should be updated to '1000.00'.");
    }

    @Test
    public void testGetDateTransaction_ShouldReturnDateTransaction() {
        Date currentDate = transaction.getDateTransaction();
        assertNotNull(currentDate, "The transaction date should not be null.");
    }

    @Test
    public void testSetDateTransaction_ShouldSetDateTransactionCorrectly() {
        Date newDate = new Date(1672457902000L); // Random timestamp
        transaction.setDateTransaction(newDate);
        assertEquals(newDate, transaction.getDateTransaction(), "The transaction date should be updated.");
    }

    @Test
    public void testGetNumCompte_ShouldReturnNumCompte() {
        assertEquals("12345", transaction.getNumCompte(), "The account number should be '12345'.");
    }

    @Test
    public void testSetNumCompte_ShouldSetNumCompteCorrectly() {
        transaction.setNumCompte("67890");
        assertEquals("67890", transaction.getNumCompte(), "The account number should be updated to '67890'.");
    }

}
