package com.mycompany.miniprojet.testDao;

import dao.*;
import entite.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

public class ClientDAOTest {

    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement clientStatement;
    @Mock
    private PreparedStatement compteStatement;
    @Mock
    private PreparedStatement authStatement;
    @Mock
    private ResultSet generatedKeys;
    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private ClientDAO clientDAO;

    private AutoCloseable openMocks;
    private Client testClient;
    private Compte testCompte;

    @BeforeEach
    void setUp() throws Exception {
        openMocks = MockitoAnnotations.openMocks(this);
        DbSingleton dbSingleton = mock(DbSingleton.class);
        when(dbSingleton.getConnection()).thenReturn(connection);

        testClient = new Client("Doe", "John", "john.doe@email.com", "123456789");
        testCompte = new Compte("ACC123", BigDecimal.valueOf(1000.00),
                new Date(System.currentTimeMillis()), "ACTIVE", 1);

        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(clientStatement);
        when(connection.prepareStatement(anyString()))
                .thenReturn(compteStatement)
                .thenReturn(authStatement);
    }

    @Test
    void createClientWithAccountAndUser_Success() throws Exception {
        // Mock database interactions
        when(clientStatement.executeUpdate()).thenReturn(1);
        when(clientStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(1);
        when(compteStatement.executeUpdate()).thenReturn(1);
        when(authStatement.executeUpdate()).thenReturn(1);

        boolean result = clientDAO.createClientWithAccountAndUser(
                testClient, testCompte, "johndoe", "password123"
        );

        assertTrue(result);
        verify(connection).commit();
        // Verify compte insertion parameters
        verify(compteStatement).setString(1, "ACC123");
        verify(compteStatement).setBigDecimal(2, BigDecimal.valueOf(1000.00));
        verify(compteStatement).setDate(3, new java.sql.Date(testCompte.getDateCreation().getTime()));
        verify(compteStatement).setString(4, "ACTIVE");
        verify(compteStatement).setInt(5, 1);
    }

    @Test
    void getClientWithAccountAndAuth_Success() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(clientStatement);
        when(clientStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        // Mock result set according to SQL join structure
        when(resultSet.getInt("clientId")).thenReturn(1);
        when(resultSet.getString("nom")).thenReturn("Doe");
        when(resultSet.getString("prenom")).thenReturn("John");
        when(resultSet.getString("email")).thenReturn("john.doe@email.com");
        when(resultSet.getString("tel")).thenReturn("123456789");
        when(resultSet.getString("authNom")).thenReturn("johndoe");
        when(resultSet.getString("motDePasse")).thenReturn("password123");
        when(resultSet.getString("role")).thenReturn("user");
        when(resultSet.getString("numCompte")).thenReturn("ACC123");
        when(resultSet.getBigDecimal("solde")).thenReturn(BigDecimal.valueOf(1000.00));
        when(resultSet.getDate("dateCreation")).thenReturn(new java.sql.Date(System.currentTimeMillis()));
        when(resultSet.getString("statut")).thenReturn("ACTIVE");

        Client result = clientDAO.getClientWithAccountAndAuth(1);

        assertNotNull(result);
        assertEquals("Doe", result.getNom());
        assertEquals("ACC123", result.getCompte().getNumCompte());
        assertEquals("user", result.getAuth().getRole());
    }

    @Test
    void updateUserAndAccount_Success() throws Exception {
        when(connection.prepareStatement(anyString()))
                .thenReturn(clientStatement)
                .thenReturn(compteStatement)
                .thenReturn(authStatement);

        when(clientStatement.executeUpdate()).thenReturn(1);
        when(compteStatement.executeUpdate()).thenReturn(1);
        when(authStatement.executeUpdate()).thenReturn(1);

        testClient.setClientId(1);
        testCompte.setNumCompte("ACC123");
        boolean result = clientDAO.updateUserAndAccount(
                testClient, testCompte, "newuser", "newpassword"
        );

        assertTrue(result);
        // Verify compte update parameters
        verify(compteStatement).setBigDecimal(1, BigDecimal.valueOf(1000.00));
        verify(compteStatement).setDate(2, new java.sql.Date(testCompte.getDateCreation().getTime()));
        verify(compteStatement).setString(3, "ACTIVE");
        verify(compteStatement).setInt(4, 1);
    }

    @Test
    void deleteUserAndAccount_CascadesSuccess() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(clientStatement);
        when(clientStatement.executeUpdate()).thenReturn(1);

        boolean result = clientDAO.deleteUserAndAccount(1);

        assertTrue(result);
        verify(clientStatement).setInt(1, 1);
        verify(connection).commit();
    }

    @Test
    void createClient_EmailUniqueConstraint() throws Exception {
        when(clientStatement.executeUpdate()).thenThrow(new SQLIntegrityConstraintViolationException("Duplicate email"));

        boolean result = clientDAO.createClientWithAccountAndUser(
                testClient, testCompte, "johndoe", "password123"
        );

        assertFalse(result);
        verify(connection).rollback();
    }
}
