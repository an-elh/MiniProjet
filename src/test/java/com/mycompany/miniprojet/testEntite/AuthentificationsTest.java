package com.mycompany.miniprojet.testEntite;
import entite.Authentification;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthentificationsTest {

    private Authentification authentification;

    @BeforeEach
    public void setUp() {
        authentification = new Authentification("admin", "password123", "admin");
    }

    @Test
    public void testSeConnecter_ValidCredentials_ShouldReturnTrue() {
        assertTrue(authentification.seConnecter("admin", "password123"), "The credentials should be valid.");
    }

    @Test
    public void testSeConnecter_InvalidCredentials_ShouldReturnFalse() {
        assertFalse(authentification.seConnecter("admin", "wrongpassword"), "The credentials should be invalid.");
    }

    @Test
    public void testGetRole_ShouldReturnRole() {
        assertEquals("admin", authentification.getRole(), "The role should be 'admin'.");
    }

    @Test
    public void testSetNom_ShouldSetNomCorrectly() {
        authentification.setNom("user");
        assertEquals("user", authentification.getNom(), "The name should be updated to 'user'.");
    }

    @Test
    public void testSetMotDePasse_ShouldSetMotDePasseCorrectly() {
        authentification.setMotDePasse("newpassword");
        assertEquals("newpassword", authentification.getMotDePasse(), "The password should be updated to 'newpassword'.");
    }

    @Test
    public void testSetRole_ShouldSetRoleCorrectly() {
        authentification.setRole("user");
        assertEquals("user", authentification.getRole(), "The role should be updated to 'user'.");
    }
}
