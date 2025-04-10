package com.mycompany.miniprojet.testDao;

import dao.*;
import entite.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CompteDAOTest {

    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private Statement statement;
    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private CompteDAO compteDAO;

    private AutoCloseable openMocks;
    private Compte testCompte;

    @BeforeEach
    void setUp() throws Exception {
        openMocks = MockitoAnnotations.openMocks(this);
        DbSingleton dbSingleton = mock(DbSingleton.class);
        when(dbSingleton.getConnection()).thenReturn(connection);

        testCompte = new Compte(
                "ACC123",
                BigDecimal.valueOf(1000.00),
                new Date(System.currentTimeMillis()),
                "ACTIVE",
                1
        );
    }

    @Test
    void createCompte_Success() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = compteDAO.createCompte(testCompte);

        assertTrue(result);
        verify(preparedStatement).setString(1, "ACC123");
        verify(preparedStatement).setBigDecimal(2, BigDecimal.valueOf(1000.00));
        verify(preparedStatement).setDate(3, new java.sql.Date(testCompte.getDateCreation().getTime()));
        verify(preparedStatement).setString(4, "ACTIVE");
        verify(preparedStatement).setInt(5, 1);
    }

    @Test
    void getCompteByNumber_Success() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        mockCompteResultSet();

        Compte result = compteDAO.getCompte("ACC123");

        assertNotNull(result);
        assertEquals("ACC123", result.getNumCompte());
        assertEquals(BigDecimal.valueOf(1000.00), result.getSolde());
    }

    @Test
    void updateSolde_DebitSuccess() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = compteDAO.updateSolde("ACC123", BigDecimal.valueOf(100.00), true);

        assertTrue(result);

        // Verify parameters
        verify(preparedStatement).setBigDecimal(1, BigDecimal.valueOf(100.00));
        verify(preparedStatement).setString(2, "ACC123");
        verify(preparedStatement).setBigDecimal(3, BigDecimal.valueOf(100.00));
    }

    @Test
    void transferSolde_Success() throws Exception {
        // Mock setup
        PreparedStatement debitStmt = mock(PreparedStatement.class);
        PreparedStatement creditStmt = mock(PreparedStatement.class);

        when(connection.prepareStatement(contains("solde - ?")))
                .thenReturn(debitStmt);
        when(debitStmt.executeUpdate()).thenReturn(1);

        when(connection.prepareStatement(contains("solde + ?")))
                .thenReturn(creditStmt);
        when(creditStmt.executeUpdate()).thenReturn(1);

        // Execute
        boolean result = compteDAO.transferSolde("ACC123", "ACC456", BigDecimal.valueOf(500.00));

        // Verify
        assertTrue(result);

        // Transaction handling
        verify(connection, times(1)).setAutoCommit(false);
        verify(connection, times(1)).commit();
        verify(connection, times(1)).setAutoCommit(true);

        // Parameter verification
        verify(debitStmt).setBigDecimal(1, BigDecimal.valueOf(500.00));
        verify(debitStmt).setString(2, "ACC123");
        verify(debitStmt).setBigDecimal(3, BigDecimal.valueOf(500.00));
        verify(creditStmt).setBigDecimal(1, BigDecimal.valueOf(500.00));
        verify(creditStmt).setString(2, "ACC456");
    }

    @Test
    void getComptesWithClientInfo_Success() throws Exception {
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        mockCompteWithClientResultSet();

        List<Compte> comptes = compteDAO.getComptes();

        assertEquals(2, comptes.size());
        Compte first = comptes.get(0);
        assertNotNull(first.getClient());
        assertEquals("John", first.getClient().getPrenom());
    }

    @Test
    void compteExiste_ReturnsTrue() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        boolean exists = compteDAO.compteExiste("ACC123");

        assertTrue(exists);
    }

    @Test
    void deleteCompteByClientId_Success() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = compteDAO.deleteCompteByClientId(1);

        assertTrue(result);
        verify(preparedStatement).setInt(1, 1);
    }

    @Test
    void updateCompte_Failure() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        boolean result = compteDAO.updateCompte(testCompte);

        assertFalse(result);
    }

    private void mockCompteResultSet() throws SQLException {
        when(resultSet.getString("numCompte")).thenReturn("ACC123");
        when(resultSet.getBigDecimal("solde")).thenReturn(BigDecimal.valueOf(1000.00));
        when(resultSet.getDate("dateCreation")).thenReturn(new java.sql.Date(System.currentTimeMillis()));
        when(resultSet.getString("statut")).thenReturn("ACTIVE");
        when(resultSet.getInt("clientId")).thenReturn(1);
    }

    private void mockCompteWithClientResultSet() throws SQLException {
        when(resultSet.getString("numCompte")).thenReturn("ACC123", "ACC456");
        when(resultSet.getBigDecimal("solde")).thenReturn(BigDecimal.valueOf(1000.00), BigDecimal.valueOf(2000.00));
        when(resultSet.getString("nom")).thenReturn("Doe", "Smith");
        when(resultSet.getString("prenom")).thenReturn("John", "Alice");
        when(resultSet.getString("email")).thenReturn("john@doe.com", "alice@smith.com");
        when(resultSet.getString("tel")).thenReturn("123456789", "987654321");
    }

    @Test
    void transferSolde_InsufficientBalance() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0); // Debit fails

        boolean result = compteDAO.transferSolde("ACC123", "ACC456", BigDecimal.valueOf(500.00));

        assertFalse(result);
        verify(connection).rollback();
    }

    @Test
    void getComptesByNameFilter_Success() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        mockCompteWithClientResultSet();

        List<Compte> comptes = compteDAO.getComptes("Doe");

        assertEquals(1, comptes.size());
        verify(preparedStatement).setString(1, "%Doe%");
    }

    @Test
    void deleteCompte_Success() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = compteDAO.deleteCompte("ACC123");

        assertTrue(result);
        verify(preparedStatement).setString(1, "ACC123");
    }
}
