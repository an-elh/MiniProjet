package com.mycompany.miniprojet.testController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import dao.*;
import entite.*;
import controller.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class ClientControllerTest {

    @Mock
    private ClientDAO clientDAO;

    @Mock
    private CompteDAO compteDAO;

    @Mock
    private AuthentificationDAO authDAO;

    @InjectMocks
    private ClientController clientController;

    private Client testClient;
    private Compte testCompte;

    @BeforeEach
    void setUp() {
        clientController = new ClientController(clientDAO, compteDAO, authDAO);

        testClient = new Client("Doe", "John", "john.doe@email.com", "123456789");
        testClient.setClientId(1);

        testCompte = new Compte(
                "ACC123",
                BigDecimal.ZERO,
                new java.sql.Date(System.currentTimeMillis()),
                "ACTIVE",
                1
        );
    }

    @Test
    void registerNewClient_Success() throws Exception {
        // Configure DAO mock
        when(clientDAO.createClientWithAccountAndUser(
                any(Client.class),
                any(Compte.class),
                eq("johndoe"),
                eq("password123")
        )).thenReturn(true);

        boolean result = clientController.registerNewClient(
                "Doe", "John", "john@doe.com", "123456789",
                "ACC123", "ACTIVE", "johndoe", "password123"
        );

        assertTrue(result);
    }

    @Test
    void generateAccountNumber_ValidFormat() {
        String accountNumber = clientController.generateAccountNumber();
        assertTrue(accountNumber.startsWith("ACC"));
        assertEquals(11, accountNumber.length());
    }

    @Test
    void getClientWithAccountAndAuth_Success() throws Exception {
        // Configure DAO mock
        when(clientDAO.getClientWithAccountAndAuth(1))
                .thenReturn(testClient);

        Client result = clientController.getClientWithAccountAndAuth(1);

        assertNotNull(result);
        assertEquals(1, result.getClientId());
    }

    @Test
    void getClient_Success() throws Exception {
        // Configure DAO mock
        when(clientDAO.getClientById(1))
                .thenReturn(testClient);

        Client result = clientController.getClient(1);

        assertNotNull(result);
        assertEquals("Doe", result.getNom());
    }

    @Test
    void deleteClientAndAccount_Success() throws Exception {
        // Configure DAO mock
        when(clientDAO.deleteUserAndAccount(1))
                .thenReturn(true);

        clientController.deleteClientAndAccount(1);

        verify(clientDAO).deleteUserAndAccount(1);
    }

    @Test
    void deleteClientAndAccount_Failure() throws Exception {
        // Configure DAO mock
        when(clientDAO.deleteUserAndAccount(1))
                .thenThrow(new SQLException("DB error"));

        clientController.deleteClientAndAccount(1);

        verify(clientDAO).deleteUserAndAccount(1);
    }

    @Test
    void updateUserAndAccountDetails_Success() throws Exception {
        // Configure DAO mocks
        when(clientDAO.getClientById(1)).thenReturn(testClient);
        when(clientDAO.updateUserAndAccount(
                any(Client.class),
                any(Compte.class),
                eq("newuser"),
                eq("newpass")
        )).thenReturn(true);

        boolean result = clientController.updateUserAndAccountDetails(
                1, "NewDoe", "John", "new@email.com", "987654321",
                "ACC456", "INACTIVE", "newuser", "newpass"
        );

        assertTrue(result);
    }

    @Test
    void updateUserAndAccountDetails_ClientNotFound() throws Exception {
        // Configure DAO mock
        when(clientDAO.getClientById(999)).thenReturn(null);

        boolean result = clientController.updateUserAndAccountDetails(
                999, "Doe", "John", "john@doe.com", "123456789",
                "ACC123", "ACTIVE", "user", "pass"
        );

        assertFalse(result);
    }
}
