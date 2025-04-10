package com.mycompany.miniprojet.testController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dao.*;
import entite.*;
import controller.*;
public class AuthentificationControllerTest {

    @Mock
    private AuthentificationDAO authDAO;

    @Mock
    private ClientDAO clientDAO;

    @InjectMocks
    private AuthentificationController authController;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        authController = new AuthentificationController();
        authController.authDAO = authDAO;
        authController.clientDAO = clientDAO;
    }

    @Test
    void testLoginAndGetClient_Success() throws SQLException {
        // Mock DAO responses
        when(authDAO.seConnecter("john", "pass123"))
                .thenReturn(new String[]{"1001"});
        when(clientDAO.getClientById(1001))
                .thenReturn(new Client("John", "Doe", "john@test.com", "123456789"));

        Client result = authController.loginAndGetClient("john", "pass123");

        assertNotNull(result);
        assertEquals("John", result.getNom());
        verify(authDAO).seConnecter("john", "pass123");
        verify(clientDAO).getClientById(1001);
    }

    @Test
    void testLoginAndGetClient_SQLException() throws SQLException {
        when(authDAO.seConnecter(anyString(), anyString()))
                .thenThrow(new SQLException("Database error"));

        Client result = authController.loginAndGetClient("john", "pass123");

        assertNull(result);
    }

    @Test
    void testAuthenticateUser_Success() throws SQLException {
        when(authDAO.getPasswordByUsername("john"))
                .thenReturn("hashed_password");

        boolean result = authController.authenticateUser("john", "hashed_password");

        assertTrue(result);
    }

    @Test
    void testLoginAndGetClientId_InvalidFormat() throws SQLException {
        when(authDAO.seConnecter("invalid", "user"))
                .thenReturn(new String[]{"invalid_id"});

        int result = authController.loginAndGetClientId("invalid", "user");

        assertEquals(-1, result);
    }

    @Test
    void testIsUsernameUnique_Available() throws SQLException {
        when(authDAO.doesUsernameExist("newuser"))
                .thenReturn(false);

        boolean result = authController.isUsernameUnique("newuser");

        assertTrue(result);
    }

    @Test
    void testLoginAndGetRole_Admin() throws SQLException {
        when(authDAO.seConnecter("admin", "adminpass"))
                .thenReturn(new String[]{"1000", "admin"});

        String[] result = authController.loginAndGetRole("admin", "adminpass");

        assertArrayEquals(new String[]{"1000", "admin"}, result);
    }

    @Test
    void testIsAdmin_True() throws SQLException {
        when(authDAO.getRole("admin"))
                .thenReturn("admin");

        boolean result = authController.isAdmin("admin");

        assertTrue(result);
    }

    @Test
    void testGetUserRole_DefaultOnError() throws SQLException {
        when(authDAO.getRole("erroruser"))
                .thenThrow(new SQLException("DB error"));

        String result = authController.getUserRole("erroruser");

        assertEquals("user", result);
    }

    @Test
    void testGetAuthentification_Success() throws Exception {
        Authentification expectedAuth = new Authentification("john", "pass123", "user");
        when(authDAO.getAuthentificationById(1001))
                .thenReturn(expectedAuth);

        Authentification result = authController.getAuthentification(1001);

        assertNotNull(result);
        assertEquals("john", result.getNom());
    }

    @Test
    void testGetAuthentification_Exception() throws Exception {
        when(authDAO.getAuthentificationById(anyInt()))
                .thenThrow(new SQLException("Not found"));

        Authentification result = authController.getAuthentification(9999);

        assertNull(result);
    }
}
