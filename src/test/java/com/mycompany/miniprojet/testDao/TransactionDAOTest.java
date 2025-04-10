package com.mycompany.miniprojet.testDao;

import entite.Transaction;
import dao.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TransactionDAOTest {

    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private TransactionDAO transactionDAO;

    private AutoCloseable openMocks;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() throws Exception {
        openMocks = MockitoAnnotations.openMocks(this);
        DbSingleton dbSingleton = mock(DbSingleton.class);
        when(dbSingleton.getConnection()).thenReturn(connection);

        testTransaction = new Transaction(
                "DEPOSIT",
                BigDecimal.valueOf(1000.00),
                new Date(),
                "ACC123"
        );
        testTransaction.setTransactionId(1);
    }

    @Test
    void createTransaction_Success() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = transactionDAO.createTransaction(testTransaction);

        assertTrue(result);
        verify(preparedStatement).setString(eq(1), eq("DEPOSIT"));
        verify(preparedStatement).setBigDecimal(eq(2), eq(BigDecimal.valueOf(1000.00)));
        verify(preparedStatement).setTimestamp(eq(3), any(Timestamp.class));
        verify(preparedStatement).setString(eq(4), eq("ACC123"));
    }

    @Test
    void createTransaction_Failure() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Database error"));

        assertThrows(SQLException.class, () -> {
            transactionDAO.createTransaction(testTransaction);
        });
    }

    @Test
    void getTransactions_Success() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        mockResultSetData();

        List<Transaction> transactions = transactionDAO.getTransactions("ACC123");

        assertEquals(2, transactions.size());

        Transaction first = transactions.get(0);
        assertEquals("DEPOSIT", first.getType());
        assertEquals(BigDecimal.valueOf(500.00), first.getMontant());

        Transaction second = transactions.get(1);
        assertEquals("WITHDRAWAL", second.getType());
        assertEquals(BigDecimal.valueOf(200.00), second.getMontant());

        verify(preparedStatement).setString(1, "ACC123");
    }

    @Test
    void getTransactions_Empty() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        List<Transaction> transactions = transactionDAO.getTransactions("ACC123");

        assertTrue(transactions.isEmpty());
    }

    @Test
    void getTransactions_Exception() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Query failed"));

        assertThrows(SQLException.class, () -> {
            transactionDAO.getTransactions("ACC123");
        });
    }

    private void mockResultSetData() throws SQLException {
        // First transaction
        when(resultSet.getInt("transactionId")).thenReturn(1, 2);
        when(resultSet.getString("type")).thenReturn("DEPOSIT", "WITHDRAWAL");
        when(resultSet.getBigDecimal("montant"))
                .thenReturn(BigDecimal.valueOf(500.00), BigDecimal.valueOf(200.00));
        when(resultSet.getTimestamp("dateTransaction"))
                .thenReturn(new Timestamp(System.currentTimeMillis()),
                        new Timestamp(System.currentTimeMillis() - 3600000));
        when(resultSet.getString("numCompte")).thenReturn("ACC123", "ACC123");
    }

    @Test
    void transactionMapping_CorrectDateConversion() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);

        long testTime = System.currentTimeMillis();
        when(resultSet.getTimestamp("dateTransaction"))
                .thenReturn(new Timestamp(testTime));

        List<Transaction> transactions = transactionDAO.getTransactions("ACC123");

        assertEquals(1, transactions.size());
        assertEquals(new Date(testTime), transactions.get(0).getDateTransaction());
    }
}
