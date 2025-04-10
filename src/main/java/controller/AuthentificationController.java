package controller;

import java.sql.SQLException;

import dao.AuthentificationDAO;
import dao.ClientDAO;
import entite.Authentification;
import entite.Client;

public class AuthentificationController {

    public AuthentificationDAO authDAO;
    public ClientDAO clientDAO;

    public AuthentificationController() {
        authDAO = new AuthentificationDAO();
        clientDAO = new ClientDAO();
    }

    // Method to authenticate a user and return the Client object
    public Client loginAndGetClient(String nom, String motDePasse) {
        try {
            String[] result = authDAO.seConnecter(nom, motDePasse);
            if (result != null && result.length > 0) {
                int clientId = Integer.parseInt(result[0]); // Extract client ID from the result
                return clientDAO.getClientById(clientId);
            }
        } catch (SQLException e) {
            // Log the exception
            System.err.println("SQL Exception during login: " + e.getMessage());
        } catch (NumberFormatException e) {
            // Log the exception for invalid number format
            System.err.println("Invalid client ID format: " + e.getMessage());
        }
        return null; // Authentication failed
    }

    // Method to check username and password against the stored credentials
    public boolean authenticateUser(String nom, String motDePasse) {
        try {
            String storedPassword = authDAO.getPasswordByUsername(nom);
            if (storedPassword != null) {
                return storedPassword.equals(motDePasse);
            }
        } catch (SQLException e) {
            // Handle exception (log it or show message to user)
            e.printStackTrace();
        }
        return false; // Authentication failed
    }

    // Method to get the client ID after successful login
    public int loginAndGetClientId(String nom, String motDePasse) {
        try {
            String[] result = authDAO.seConnecter(nom, motDePasse);
            if (result != null && result.length > 0) {
                return Integer.parseInt(result[0]); // Extract client ID from the result
            }
        } catch (SQLException e) {
            // Log the exception
            System.err.println("Error during login: " + e.getMessage());
        } catch (NumberFormatException e) {
            // Log the exception for invalid number format
            System.err.println("Invalid client ID format: " + e.getMessage());
        }
        return -1; // Return failure value if an error occurs
    }

    // Check if the username is unique (not already taken)
    public boolean isUsernameUnique(String nom) {
        try {
            return !authDAO.doesUsernameExist(nom);
        } catch (SQLException e) {
            // Handle exception (log it or show message to user)
            e.printStackTrace();
        }
        return false; // If there's an error, assume the username is not unique
    }

    // Method to authenticate and get role
    public String[] loginAndGetRole(String nom, String motDePasse) {
        try {
            String[] result = authDAO.seConnecter(nom, motDePasse);
            if (result != null) {
                return result; // Returns [clientId, role]
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to check if user is admin
    public boolean isAdmin(String nom) {
        try {
            String role = authDAO.getRole(nom);
            return "admin".equals(role);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to get user's role
    public String getUserRole(String nom) {
        try {
            return authDAO.getRole(nom);
        } catch (SQLException e) {
            e.printStackTrace();
            return "user"; // Default to user role on error
        }
    }

    public Authentification getAuthentification(int clientId) {
        try {
            System.out.println("Authentification :" + authDAO.getAuthentificationById(clientId));
            return authDAO.getAuthentificationById(clientId);
        } catch (Exception e) {
            return null;
        }
    }
}
