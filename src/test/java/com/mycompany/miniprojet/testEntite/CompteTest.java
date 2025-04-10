package com.mycompany.miniprojet.testEntite;

import entite.Client;
import entite.Compte;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

public class CompteTest {

    private Compte compte;
    private Client client;

    @BeforeEach
    public void setUp() {
        client = new Client();
        compte = new Compte("12345", new BigDecimal("1000.00"), Date.valueOf("2025-04-09"), "active", 1);
        compte.setClient(client);
    }

    @Test
    public void testGetNumCompte_ShouldReturnNumCompte() {
        assertEquals("12345", compte.getNumCompte(), "The account number should be '12345'.");
    }

    @Test
    public void testGetSolde_ShouldReturnSolde() {
        assertEquals(new BigDecimal("1000.00"), compte.getSolde(), "The balance should be '1000.00'.");
    }

    @Test
    public void testGetDateCreation_ShouldReturnDateCreation() {
        assertEquals(Date.valueOf("2025-04-09"), compte.getDateCreation(), "The creation date should be '2025-04-09'.");
    }

    @Test
    public void testGetStatut_ShouldReturnStatut() {
        assertEquals("active", compte.getStatut(), "The status should be 'active'.");
    }

    @Test
    public void testGetClientId_ShouldReturnClientId() {
        assertEquals(1, compte.getClientId(), "The clientId should be 1.");
    }

    @Test
    public void testSetNumCompte_ShouldSetNumCompteCorrectly() {
        compte.setNumCompte("67890");
        assertEquals("67890", compte.getNumCompte(), "The account number should be updated to '67890'.");
    }

    @Test
    public void testSetSolde_ShouldSetSoldeCorrectly() {
        compte.setSolde(new BigDecimal("2000.00"));
        assertEquals(new BigDecimal("2000.00"), compte.getSolde(), "The balance should be updated to '2000.00'.");
    }

    @Test
    public void testSetDateCreation_ShouldSetDateCreationCorrectly() {
        compte.setDateCreation(Date.valueOf("2025-01-01"));
        assertEquals(Date.valueOf("2025-01-01"), compte.getDateCreation(), "The creation date should be updated.");
    }

    @Test
    public void testSetStatut_ShouldSetStatutCorrectly() {
        compte.setStatut("inactive");
        assertEquals("inactive", compte.getStatut(), "The status should be updated to 'inactive'.");
    }

    @Test
    public void testDepositerArgent_ShouldIncreaseSolde() {
        compte.deposerArgent(new BigDecimal("500.00"));
        assertEquals(new BigDecimal("1500.00"), compte.getSolde(), "The balance should be increased by '500.00'.");
    }

    @Test
    public void testDepositerArgent_WithInvalidAmount_ShouldNotChangeSolde() {
        compte.deposerArgent(new BigDecimal("-100.00"));
        assertEquals(new BigDecimal("1000.00"), compte.getSolde(), "The balance should remain the same for invalid deposit.");
    }

    @Test
    public void testRetirerArgent_ShouldDecreaseSolde() {
        compte.retirerArgent(new BigDecimal("200.00"));
        assertEquals(new BigDecimal("800.00"), compte.getSolde(), "The balance should be decreased by '200.00'.");
    }

    @Test
    public void testRetirerArgent_WithInvalidAmount_ShouldNotChangeSolde() {
        compte.retirerArgent(new BigDecimal("-100.00"));
        assertEquals(new BigDecimal("1000.00"), compte.getSolde(), "The balance should remain the same for invalid withdrawal.");
    }

    @Test
    public void testRetirerArgent_WithInsufficientFunds_ShouldNotChangeSolde() {
        compte.retirerArgent(new BigDecimal("1500.00"));
        assertEquals(new BigDecimal("1000.00"), compte.getSolde(), "The balance should remain the same when withdrawal exceeds available funds.");
    }

    @Test
    public void testConsulterSolde_ShouldReturnCorrectBalance() {
        assertEquals(new BigDecimal("1000.00"), compte.consulterSolde(), "The balance should be '1000.00'.");
    }

    @Test
    public void testToString_ShouldReturnCorrectString() {
        String expected = "Compte [numCompte=12345, solde=1000.00, dateCreation=2025-04-09, statut=active]";
        assertEquals(expected, compte.toString(), "The toString() method should return the correct string.");
    }
}
