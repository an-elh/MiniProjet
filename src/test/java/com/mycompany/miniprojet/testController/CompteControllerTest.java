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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dao.*;
import entite.*;
import controller.*;

@ExtendWith(MockitoExtension.class)

public class CompteControllerTest {

    @Mock
    private CompteDAO compteDAO;

    @Mock
    private TransactionDAO transactionDAO;

    @InjectMocks
    private CompteController compteController;

    private Compte testCompte;
    private final String testAccountNumber = "ACC123456";

    @BeforeEach
    void setUp() {
        // Initialize test account
        testCompte = new Compte(
            testAccountNumber,
            BigDecimal.valueOf(1000),
            new Date(System.currentTimeMillis()),
            "ACTIVE",
            1
        );
    }

    @Test
    void testCreateCompte_Success() throws SQLException {
        when(compteDAO.createCompte(any(Compte.class))).thenReturn(true);
        
        boolean result = compteController.createCompte(testCompte);
        
        assertTrue(result);
        verify(compteDAO).createCompte(testCompte);
    }

    @Test
    void testCreateCompte_Failure() throws SQLException {
        when(compteDAO.createCompte(any(Compte.class))).thenThrow(new SQLException("DB error"));
        
        boolean result = compteController.createCompte(testCompte);
        
        assertFalse(result);
    }

    @Test
    void testGetCompte_Exists() throws SQLException {
        when(compteDAO.getCompte(testAccountNumber)).thenReturn(testCompte);
        
        Compte result = compteController.getCompte(testAccountNumber);
        
        assertEquals(testAccountNumber, result.getNumCompte());
    }

    @Test
    void testGetCompte_NotExists() throws SQLException {
        when(compteDAO.getCompte("UNKNOWN")).thenReturn(null);
        
        Compte result = compteController.getCompte("UNKNOWN");
        
        assertNull(result);
    }

    @Test
    void testGetAllComptes_Success() throws SQLException {
        List<Compte> expected = Arrays.asList(testCompte);
        when(compteDAO.getComptes()).thenReturn(expected);
        
        List<Compte> result = compteController.getAllComptes();
        
        assertEquals(1, result.size());
        assertEquals(testAccountNumber, result.get(0).getNumCompte());
    }

    @Test
    void testUpdateSolde_Success() throws SQLException {
        when(compteDAO.updateSolde(testAccountNumber, BigDecimal.valueOf(1500))).thenReturn(true);
        
        boolean result = compteController.updateSolde(testAccountNumber, BigDecimal.valueOf(1500));
        
        assertTrue(result);
    }

    @Test
    void testDeposit_Success() throws SQLException {
        when(compteDAO.getCompte(testAccountNumber)).thenReturn(testCompte);
        when(compteDAO.updateSolde(eq(testAccountNumber), any(BigDecimal.class))).thenReturn(true);
        
        boolean result = compteController.deposit(testAccountNumber, BigDecimal.valueOf(500));
        
        assertTrue(result);
        verify(transactionDAO).createTransaction(any(Transaction.class));
    }

    @Test
    void testWithdraw_Success() throws SQLException {
        when(compteDAO.getCompte(testAccountNumber)).thenReturn(testCompte);
        when(compteDAO.updateSolde(eq(testAccountNumber), any(BigDecimal.class))).thenReturn(true);
        
        boolean result = compteController.withdraw(testAccountNumber, BigDecimal.valueOf(500));
        
        assertTrue(result);
        verify(transactionDAO).createTransaction(any(Transaction.class));
    }

    @Test
    void testWithdraw_InsufficientBalance() throws SQLException {
        testCompte.setSolde(BigDecimal.valueOf(300));
        when(compteDAO.getCompte(testAccountNumber)).thenReturn(testCompte);
        
        boolean result = compteController.withdraw(testAccountNumber, BigDecimal.valueOf(500));
        
        assertFalse(result);
        verifyNoInteractions(transactionDAO);
    }

    @Test
    void testEffectuerVirement_Success() throws SQLException {
        Compte sender = new Compte("ACC1", BigDecimal.valueOf(1000), new Date(System.currentTimeMillis()), "ACTIVE", 1);
        Compte receiver = new Compte("ACC2", BigDecimal.valueOf(500), new Date(System.currentTimeMillis()), "ACTIVE", 2);
        
        when(compteDAO.getCompte("ACC1")).thenReturn(sender);
        when(compteDAO.getCompte("ACC2")).thenReturn(receiver);
        when(compteDAO.updateSolde("ACC1", BigDecimal.valueOf(500))).thenReturn(true);
        when(compteDAO.updateSolde("ACC2", BigDecimal.valueOf(1000))).thenReturn(true);
        
        boolean result = compteController.effectuerVirement("ACC1", "ACC2", BigDecimal.valueOf(500));
        
        assertTrue(result);
    }

    @Test
    void testEffectuerVirement_SenderNotFound() throws SQLException {
        when(compteDAO.getCompte("ACC1")).thenReturn(null);
        
        boolean result = compteController.effectuerVirement("ACC1", "ACC2", BigDecimal.valueOf(500));
        
        assertFalse(result);
    }

    @Test
    void testEffectuerVirement_InsufficientBalance() throws SQLException {
        Compte sender = new Compte("ACC1", BigDecimal.valueOf(300), new Date(System.currentTimeMillis()), "ACTIVE", 1);
        when(compteDAO.getCompte("ACC1")).thenReturn(sender);
        
        boolean result = compteController.effectuerVirement("ACC1", "ACC2", BigDecimal.valueOf(500));
        
        assertFalse(result);
    }
}