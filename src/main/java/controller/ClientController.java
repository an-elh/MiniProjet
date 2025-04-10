package controller;

import dao.AuthentificationDAO;
import dao.ClientDAO;
import dao.CompteDAO;
import entite.Authentification;
import entite.Client;
import entite.Compte;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

public class ClientController {

    private final ClientDAO clientDAO;
    private final CompteDAO compteDAO;
    private final AuthentificationDAO authDAO;

    public ClientController() {
        this.clientDAO = new ClientDAO();
        this.compteDAO = new CompteDAO();
        this.authDAO = new AuthentificationDAO();
    }
    public ClientController(ClientDAO clientDAO, CompteDAO compteDAO, AuthentificationDAO authDAO) {
        this.clientDAO = clientDAO;
        this.compteDAO = compteDAO;
        this.authDAO = authDAO;
    }
    /**
     * Creates a new client, associated bank account, and authentication user.
     */
    public boolean registerNewClient(String nom, String prenom, String email, String tel, String numComp, String statut,
            String username, String password) {
        // Input validation
        if (isEmpty(nom) || isEmpty(prenom) || isEmpty(email) || isEmpty(tel)
                || isEmpty(username) || isEmpty(password)) {
            return false;
        }

        try {
            // Create Client object
            Client client = new Client();
            client.setNom(nom);
            client.setPrenom(prenom);
            client.setEmail(email);
            client.setTel(tel);

            // Create Account object
            Compte compte = new Compte();
            compte.setNumCompte(numComp);
            compte.setSolde(new BigDecimal("0.00")); // Start with zero balance
            compte.setDateCreation(new java.sql.Date(System.currentTimeMillis()));
            compte.setStatut(statut);

            // Call DAO method to create client, account, and user
            boolean success = clientDAO.createClientWithAccountAndUser(client, compte, username, password);

            if (success) {
                System.out.println("Client registered successfully with account number: " + compte.getNumCompte());
            } else {
                System.err.println("Failed to register client");
            }

            return success;

        } catch (Exception e) {
            System.err.println("Error in client registration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserAndAccountDetails(int clientId, String nom, String prenom, String email, String tel,
            String numCompte, String statut, String username, String newPassword) {
        // Input validation
        if (isEmpty(nom) || isEmpty(prenom) || isEmpty(email) || isEmpty(tel)
                || isEmpty(username) || isEmpty(newPassword) || isEmpty(numCompte) || isEmpty(statut)) {
            return false; // Input validation failed
        }

        try {
            // Fetch the existing client and account data (if necessary)
            Client client = clientDAO.getClientById(clientId);
            if (client == null) {
                System.err.println("Client not found with clientId: " + clientId);
                return false; // Client not found
            }

            // Update Client object
            client.setNom(nom);
            client.setPrenom(prenom);
            client.setEmail(email);
            client.setTel(tel);

            // Create and Update Compte object
            Compte compte = new Compte();
            compte.setClientId(clientId);
            compte.setNumCompte(numCompte);
            compte.setStatut(statut);
            compte.setSolde(new BigDecimal("0.00"));  // Set the appropriate balance if necessary
            compte.setDateCreation(new java.sql.Date(System.currentTimeMillis())); // Update creation date if needed

            // Call DAO method to update client, account, and authentification
            boolean success = clientDAO.updateUserAndAccount(client, compte, username, newPassword);

            if (success) {
                System.out.println("Client, account, and authentication details updated successfully.");
            } else {
                System.err.println("Failed to update client, account, or authentication details.");
            }

            return success;
        } catch (SQLException e) {
            System.err.println("Error while updating user and account details: " + e.getMessage());
            e.printStackTrace();
            return false; // Return false in case of an error
        }
    }

    public void deleteClientAndAccount(int clientId) {
        try {
            // Call the DAO method to delete the user and associated data
            boolean success = clientDAO.deleteUserAndAccount(clientId);

            if (success) {
                System.out.println("Client and associated data deleted successfully.");
            } else {
                System.out.println("Failed to delete client and associated data.");
            }

        } catch (SQLException e) {
            System.err.println("Error occurred while deleting client and associated data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String generateAccountNumber() {
        // Generate a unique account number with prefix "ACC" followed by timestamp
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(5);
        return "ACC" + timestamp;
    }

    public boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public Client getClient(int clientId) {
        try {
            return clientDAO.getClientById(clientId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Client getClientWithAccountAndAuth(int clientId) {
        try {
            return clientDAO.getClientWithAccountAndAuth(clientId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
