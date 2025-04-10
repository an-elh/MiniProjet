package dao;

import entite.Authentification;
import entite.Client;
import entite.Compte;
import java.sql.*;

public class ClientDAO {

    private Connection con;

    public ClientDAO() {
        this.con = DbSingleton.getInstance().getConnection();
    }

    public boolean createClientWithAccountAndUser(Client client, Compte compte, String nom, String motDePasse) {
        String clientSql = "INSERT INTO Client (nom, prenom, email, tel) VALUES (?, ?, ?, ?)";
        String compteSql = "INSERT INTO Compte (numCompte, solde, dateCreation, statut, clientId) VALUES (?, ?, ?, ?, ?)";
        String userSql = "INSERT INTO Authentification (nom, motDePasse, clientId) VALUES (?, ?, ?)";

        try{
            // Start transaction
            con.setAutoCommit(false);

            try (PreparedStatement clientStmt = con.prepareStatement(clientSql, Statement.RETURN_GENERATED_KEYS); 
                    PreparedStatement compteStmt = con.prepareStatement(compteSql); 
                    PreparedStatement userStmt = con.prepareStatement(userSql)) {

                // Insert Client
                clientStmt.setString(1, client.getNom());
                clientStmt.setString(2, client.getPrenom());
                clientStmt.setString(3, client.getEmail());
                clientStmt.setString(4, client.getTel());
                int clientRowsAffected = clientStmt.executeUpdate();

                if (clientRowsAffected > 0) {
                    try (ResultSet generatedKeys = clientStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int clientId = generatedKeys.getInt(1);
                            client.setClientId(clientId);
                            compte.setClientId(clientId);

                            // Insert Compte
                            compteStmt.setString(1, compte.getNumCompte());
                            compteStmt.setBigDecimal(2, compte.getSolde());
                            compteStmt.setDate(3, compte.getDateCreation());
                            compteStmt.setString(4, compte.getStatut());
                            compteStmt.setInt(5, clientId);
                            int compteRowsAffected = compteStmt.executeUpdate();

                            if (compteRowsAffected > 0) {
                                // Insert User (Authentication)
                                userStmt.setString(1, nom);
                                userStmt.setString(2, motDePasse);
                                userStmt.setInt(3, clientId);
                                int userRowsAffected = userStmt.executeUpdate();

                                if (userRowsAffected > 0) {
                                    con.commit();
                                    return true;
                                } else {
                                    con.rollback();
                                    System.err.println("Failed to create authentication record");
                                    return false;
                                }
                            } else {
                                con.rollback();
                                System.err.println("Failed to create account record");
                                return false;
                            }
                        } else {
                            con.rollback();
                            System.err.println("Failed to retrieve generated client ID");
                            return false;
                        }
                    }
                } else {
                    con.rollback();
                    System.err.println("Failed to create client record");
                    return false;
                }
            } catch (SQLException e) {
                con.rollback();
                System.err.println("SQL error during client creation: " + e.getMessage());
                e.printStackTrace();
                return false;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateClient(Client client) throws SQLException {
        String sql = "UPDATE Client SET nom = ?, prenom = ?, email = ?, tel = ? WHERE clientId = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getTel());
            stmt.setInt(5, client.getClientId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteClient(int clientId) throws SQLException {
        String sql = "DELETE FROM Client WHERE clientId = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, clientId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteUserAndAccount(int clientId) throws SQLException {
        String deleteClientSql = "DELETE FROM Client WHERE clientId = ?";
        // Start a transaction to delete from all tables

        try (PreparedStatement clientStmt = con.prepareStatement(deleteClientSql);) {

            // Delete from Client table
            clientStmt.setInt(1, clientId);
            int clientRowsAffected = clientStmt.executeUpdate();

            // Check if all deletions were successful
            if (clientRowsAffected > 0) {
                // Commit the transaction if all deletions succeeded
                con.commit();
                return true; // Successful deletion
            } else {
                // Rollback the transaction if any deletion failed
                con.rollback();
                return false; // Deletion failed
            }

        } catch (SQLException e) {
            // Rollback if any exception occurs
            con.rollback();
            throw new SQLException("Error deleting user and account data", e);
        } finally {
            // Reset auto-commit to true after the transaction is completed
            con.setAutoCommit(true);
        }
    }

    public Client getClientById(int clientId) {
        String query = "SELECT * FROM Client WHERE clientId = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Client client = new Client();
                client.setClientId(rs.getInt("clientId"));
                client.setNom(rs.getString("nom"));
                client.setPrenom(rs.getString("prenom"));
                client.setEmail(rs.getString("email"));
                client.setTel(rs.getString("tel"));
                return client;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Client getClientWithAccountAndAuth(int clientId) throws SQLException {
        String query = "SELECT c.clientId, c.nom, c.prenom, c.email, c.tel, "
                + "a.nom AS authNom, a.motDePasse, a.role, "
                + "co.numCompte, co.solde, co.dateCreation, co.statut "
                + "FROM Client c "
                + "INNER JOIN Authentification a ON c.clientId = a.clientId "
                + "INNER JOIN Compte co ON c.clientId = co.clientId "
                + "WHERE c.clientId = ?";

        try (PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Create Client object
                    Client client = new Client();
                    client.setClientId(rs.getInt("clientId"));
                    client.setNom(rs.getString("nom"));
                    client.setPrenom(rs.getString("prenom"));
                    client.setEmail(rs.getString("email"));
                    client.setTel(rs.getString("tel"));

                    // Create Authentification object
                    Authentification auth = new Authentification(
                            rs.getString("authNom"),
                            rs.getString("motDePasse"),
                            rs.getString("role")
                    );
                    client.setAuth(auth);

                    // Create Compte object
                    Compte compte = new Compte(
                            rs.getString("numCompte"),
                            rs.getBigDecimal("solde"),
                            rs.getDate("dateCreation"),
                            rs.getString("statut"),
                            clientId
                    );
                    client.setCompte(compte);

                    return client;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error retrieving client with account and authentication", e);
        }
        return null;
    }

    public boolean updateUserAndAccount(Client client, Compte compte, String user, String newPassword) throws SQLException {
        String updateClientSql = "UPDATE Client SET nom = ?, prenom = ?, email = ?, tel = ? WHERE clientId = ?";
        String updateCompteSql = "UPDATE Compte SET solde = ?, dateCreation = ?, statut = ? WHERE clientId = ?";
        String updateAuthSql = "UPDATE Authentification SET motDePasse = ?, nom = ? WHERE clientId = ?";

        try (PreparedStatement clientStmt = con.prepareStatement(updateClientSql); PreparedStatement compteStmt = con.prepareStatement(updateCompteSql); PreparedStatement authStmt = con.prepareStatement(updateAuthSql)) {

            // Update Client
            clientStmt.setString(1, client.getNom());
            clientStmt.setString(2, client.getPrenom());
            clientStmt.setString(3, client.getEmail());
            clientStmt.setString(4, client.getTel());
            clientStmt.setInt(5, client.getClientId());
            int clientRowsAffected = clientStmt.executeUpdate();

            // Update Compte
            compteStmt.setBigDecimal(1, compte.getSolde());
            compteStmt.setDate(2, compte.getDateCreation());
            compteStmt.setString(3, compte.getStatut());
            compteStmt.setInt(4, compte.getClientId());
            int compteRowsAffected = compteStmt.executeUpdate();

            // Update Authentification
            authStmt.setString(1, newPassword);
            authStmt.setString(2, user);
            authStmt.setInt(3, client.getClientId());
            int authRowsAffected = authStmt.executeUpdate();

            // Check if all updates were successful
            if (clientRowsAffected > 0 && compteRowsAffected > 0 && authRowsAffected > 0) {
                // Commit the transaction if all updates succeeded
                con.commit();
                return true;
            } else {
                // Rollback the transaction if any update failed
                con.rollback();
                return false;
            }

        } catch (SQLException e) {
            // Rollback if any exception occurs
            con.rollback();
            throw new SQLException("Error updating user and account data", e);
        } finally {
            // Reset auto-commit to true after the transaction is completed
            con.setAutoCommit(true);
        }
    }
}
