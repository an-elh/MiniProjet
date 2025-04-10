package com.mycompany.miniprojet.testEntite;

import entite.Authentification;
import entite.Client;
import entite.Compte;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

public class ClientTest {
    
    private Client client;
    private Authentification auth;
    private Compte compte;

    @BeforeEach
    public void setUp() {
        auth = new Authentification("user", "password", "user");
        compte = new Compte();
        client = new Client();
        client.setClientId(1);
        client.setNom("Doe");
        client.setPrenom("John");
        client.setEmail("john.doe@example.com");
        client.setTel("1234567890");
        client.setAuth(auth);
        client.setCompte(compte);
    }

    @Test
    public void testGetClientId_ShouldReturnClientId() {
        assertEquals(1, client.getClientId(), "The clientId should be 1.");
    }

    @Test
    public void testGetNom_ShouldReturnNom() {
        assertEquals("Doe", client.getNom(), "The name should be 'Doe'.");
    }

    @Test
    public void testGetPrenom_ShouldReturnPrenom() {
        assertEquals("John", client.getPrenom(), "The first name should be 'John'.");
    }

    @Test
    public void testGetEmail_ShouldReturnEmail() {
        assertEquals("john.doe@example.com", client.getEmail(), "The email should be 'john.doe@example.com'.");
    }

    @Test
    public void testGetTel_ShouldReturnTel() {
        assertEquals("1234567890", client.getTel(), "The phone number should be '1234567890'.");
    }

    @Test
    public void testGetAuth_ShouldReturnAuth() {
        assertEquals(auth, client.getAuth(), "The authentication should match.");
    }

    @Test
    public void testGetCompte_ShouldReturnCompte() {
        assertEquals(compte, client.getCompte(), "The account should match.");
    }

    @Test
    public void testSetNom_ShouldSetNomCorrectly() {
        client.setNom("Smith");
        assertEquals("Smith", client.getNom(), "The name should be updated to 'Smith'.");
    }

    @Test
    public void testSetPrenom_ShouldSetPrenomCorrectly() {
        client.setPrenom("Jane");
        assertEquals("Jane", client.getPrenom(), "The first name should be updated to 'Jane'.");
    }

    @Test
    public void testSetEmail_ShouldSetEmailCorrectly() {
        client.setEmail("jane.smith@example.com");
        assertEquals("jane.smith@example.com", client.getEmail(), "The email should be updated.");
    }

    @Test
    public void testSetTel_ShouldSetTelCorrectly() {
        client.setTel("9876543210");
        assertEquals("9876543210", client.getTel(), "The phone number should be updated.");
    }

    @Test
    public void testSetAuth_ShouldSetAuthCorrectly() {
        Authentification newAuth = new Authentification("admin", "adminpass", "admin");
        client.setAuth(newAuth);
        assertEquals(newAuth, client.getAuth(), "The authentication should be updated.");
    }

    @Test
    public void testSetCompte_ShouldSetCompteCorrectly() {
        Compte newCompte = new Compte();
        client.setCompte(newCompte);
        assertEquals(newCompte, client.getCompte(), "The account should be updated.");
    }
}