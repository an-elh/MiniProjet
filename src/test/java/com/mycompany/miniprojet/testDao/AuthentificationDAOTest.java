package com.mycompany.miniprojet.testDao;

import entite.Authentification;
import dao.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Nested;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class AuthentificationDAOTest {

    @Nested
    class DatabaseOperationTests {
        @Mock
        private Connection mockConnection;
        
        @Mock
        private PreparedStatement mockPreparedStatement;
        
        @Mock
        private ResultSet mockResultSet;
        
        @Mock
        private DbSingleton mockDbSingleton;
        
        @InjectMocks
        private AuthentificationDAO authentificationDAO;
        
        @BeforeEach
        public void setUp() throws SQLException {
            // Initialize mocks
            MockitoAnnotations.openMocks(this);
            
            // Set up DbSingleton mock
            when(mockDbSingleton.getConnection()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            
            // Create a new DAO instance for each test
            authentificationDAO = new AuthentificationDAO();
            
            try {
                java.lang.reflect.Field field = DbSingleton.class.getDeclaredField("instance");
                field.setAccessible(true);
                field.set(null, mockDbSingleton);
            } catch (Exception e) {
                fail("Failed to set up DbSingleton mock: " + e.getMessage());
            }
        }
        
        @Test
        public void testCreateUser_Success() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);
            
            // Act
            boolean result = authentificationDAO.createUser("testUser", "password123", 1);
            
            // Assert
            assertTrue(result);
            verify(mockPreparedStatement).setString(1, "testUser");
            verify(mockPreparedStatement).setString(2, "password123");
            verify(mockPreparedStatement).setInt(3, 1);
            verify(mockPreparedStatement).executeUpdate();
        }
        
        @Test
        public void testCreateUser_Failure() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeUpdate()).thenReturn(0);
            
            // Act
            boolean result = authentificationDAO.createUser("testUser", "password123", 1);
            
            // Assert
            assertFalse(result);
            verify(mockPreparedStatement).executeUpdate();
        }
        
        @Test
        public void testCreateUser_SQLException() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Database error"));
            
            // Act & Assert
            assertThrows(SQLException.class, () -> 
                authentificationDAO.createUser("testUser", "password123", 1)
            );
        }
        
        @Test
        public void testGetPasswordByUsername_Found() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getString("motDePasse")).thenReturn("password123");
            
            // Act
            String result = authentificationDAO.getPasswordByUsername("testUser");
            
            // Assert
            assertEquals("password123", result);
            verify(mockPreparedStatement).setString(1, "testUser");
            verify(mockResultSet).getString("motDePasse");
        }
        
        @Test
        public void testGetPasswordByUsername_NotFound() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);
            
            // Act
            String result = authentificationDAO.getPasswordByUsername("nonExistentUser");
            
            // Assert
            assertNull(result);
            verify(mockPreparedStatement).setString(1, "nonExistentUser");
        }
        
        @Test
        public void testGetPasswordByUsername_SQLException() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));
            
            // Act & Assert
            assertThrows(SQLException.class, () -> 
                authentificationDAO.getPasswordByUsername("testUser")
            );
        }
        
        @Test
        public void testDoesUsernameExist_True() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            
            // Act
            boolean result = authentificationDAO.doesUsernameExist("existingUser");
            
            // Assert
            assertTrue(result);
            verify(mockPreparedStatement).setString(1, "existingUser");
        }
        
        @Test
        public void testDoesUsernameExist_False() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);
            
            // Act
            boolean result = authentificationDAO.doesUsernameExist("nonExistentUser");
            
            // Assert
            assertFalse(result);
            verify(mockPreparedStatement).setString(1, "nonExistentUser");
        }
        
        @Test
        public void testDoesUsernameExist_SQLException() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));
            
            // Act & Assert
            assertThrows(SQLException.class, () -> 
                authentificationDAO.doesUsernameExist("testUser")
            );
        }
        
        @Test
        public void testSeConnecter_Success() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getString("motDePasse")).thenReturn("password123");
            when(mockResultSet.getInt("clientId")).thenReturn(1);
            when(mockResultSet.getString("role")).thenReturn("user");
            
            // Act
            String[] result = authentificationDAO.seConnecter("testUser", "password123");
            
            // Assert
            assertNotNull(result);
            assertEquals("1", result[0]);
            assertEquals("user", result[1]);
            verify(mockPreparedStatement).setString(1, "testUser");
        }
        
        @Test
        public void testSeConnecter_WrongPassword() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getString("motDePasse")).thenReturn("correctPassword");
            
            // Act
            String[] result = authentificationDAO.seConnecter("testUser", "wrongPassword");
            
            // Assert
            assertNull(result);
            verify(mockPreparedStatement).setString(1, "testUser");
        }
        
        @Test
        public void testSeConnecter_UserNotFound() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);
            
            // Act
            String[] result = authentificationDAO.seConnecter("nonExistentUser", "anyPassword");
            
            // Assert
            assertNull(result);
            verify(mockPreparedStatement).setString(1, "nonExistentUser");
        }
        
        @Test
        public void testSeConnecter_SQLException() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));
            
            // Act & Assert
            assertThrows(SQLException.class, () -> 
                authentificationDAO.seConnecter("testUser", "password123")
            );
        }
        
        @Test
        public void testGetRole_Found() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getString("role")).thenReturn("admin");
            
            // Act
            String result = authentificationDAO.getRole("admin");
            
            // Assert
            assertEquals("admin", result);
            verify(mockPreparedStatement).setString(1, "admin");
        }
        
        @Test
        public void testGetRole_NotFound() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);
            
            // Act
            String result = authentificationDAO.getRole("nonExistentUser");
            
            // Assert
            assertEquals("user", result);
            verify(mockPreparedStatement).setString(1, "nonExistentUser");
        }
        
        @Test
        public void testGetRole_SQLException() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));
            
            // Act & Assert
            assertThrows(SQLException.class, () -> 
                authentificationDAO.getRole("testUser")
            );
        }
        
        @Test
        public void testGetAuthentificationById_Found() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getString("nom")).thenReturn("testUser");
            when(mockResultSet.getString("motDePasse")).thenReturn("password123");
            when(mockResultSet.getString("role")).thenReturn("user");
            
            // Act
            Authentification result = authentificationDAO.getAuthentificationById(1);
            
            // Assert
            assertNotNull(result);
            assertEquals("testUser", result.getNom());
            assertEquals("password123", result.getMotDePasse());
            assertEquals("user", result.getRole());
            verify(mockPreparedStatement).setInt(1, 1);
        }
        
        @Test
        public void testGetAuthentificationById_NotFound() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);
            
            // Act
            Authentification result = authentificationDAO.getAuthentificationById(999);
            
            // Assert
            assertNull(result);
            verify(mockPreparedStatement).setInt(1, 999);
        }
        
        @Test
        public void testGetAuthentificationById_SQLException() throws SQLException {
            // Arrange
            when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));
            
            // Act & Assert
            assertThrows(SQLException.class, () -> 
                authentificationDAO.getAuthentificationById(1)
            );
        }
    }
    
    /**
     * Simple tests that don't require DB mocking
     */
    @Test
    public void testGettersAndSetters() {
        AuthentificationDAO dao = new AuthentificationDAO("testUser", "password123");
        
        // Test getters
        assertEquals("testUser", dao.getNom());
        
        // Test setters
        dao.setNom("newUser");
        assertEquals("newUser", dao.getNom());
        
        // Test setting password
        dao.setMotDePasse("newPassword");
    }
}