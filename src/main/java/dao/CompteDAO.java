package dao;

import entite.Client;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entite.Compte;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CompteDAO {

    private Connection connection;

    // Constructor that gets the connection from the DbSingleton
    public CompteDAO() {
        this.connection = DbSingleton.getInstance().getConnection();
    }

    // Method to insert a new Compte into the database
    public boolean createCompte(Compte compte) throws SQLException {
        String query = "INSERT INTO Compte (numCompte, solde, dateCreation, statut, clientId) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, compte.getNumCompte());
            stmt.setBigDecimal(2, compte.getSolde());
            stmt.setDate(3, compte.getDateCreation());
            stmt.setString(4, compte.getStatut());
            stmt.setInt(5, compte.getClientId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    // Delete a Compte by numCompte

    public boolean deleteCompte(String numCompte) throws SQLException {
        String query = "DELETE FROM Compte WHERE numCompte = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, numCompte);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean updateCompte(Compte compte) throws SQLException {
        String sql = "UPDATE Compte SET solde = ?, dateCreation = ?, statut = ? WHERE clientId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, compte.getSolde());
            stmt.setDate(2, compte.getDateCreation());
            stmt.setString(3, compte.getStatut());
            stmt.setInt(4, compte.getClientId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Delete a Compte by clientId
    public boolean deleteCompteByClientId(int clientId) throws SQLException {
        String query = "DELETE FROM Compte WHERE clientId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Method to retrieve a Compte by account number (numCompte)
    public Compte getCompte(String numCompte) throws SQLException {
        String query = "SELECT * FROM Compte WHERE numCompte = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, numCompte);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String accountNum = rs.getString("numCompte");
                BigDecimal solde = rs.getBigDecimal("solde");
                Date dateCreation = rs.getDate("dateCreation");
                String statut = rs.getString("statut");
                int clientId = rs.getInt("clientId");

                return new Compte(accountNum, solde, dateCreation, statut, clientId);
            }
        }
        return null;
    }

    // Method to retrieve a Compte by Client ID (clientId)
    public Compte getCompte(int clientId) throws SQLException {
        String query = "SELECT * FROM Compte WHERE clientId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String accountNum = rs.getString("numCompte");
                    BigDecimal solde = rs.getBigDecimal("solde");
                    Date dateCreation = rs.getDate("dateCreation");
                    String statut = rs.getString("statut");
                    int clientID = rs.getInt("clientId");

                    // Create and return a Compte object
                    return new Compte(accountNum, solde, dateCreation, statut, clientID);
                }
            }
        }
        return null; // Return null if no account found for the clientId
    }

    public List<Compte> getComptes() throws SQLException {
        String query = """
        SELECT c.numCompte, c.solde, c.dateCreation, c.statut, c.clientId,
               cl.nom, cl.prenom, cl.email, cl.tel
        FROM Compte c
        INNER JOIN Client cl ON c.clientId = cl.clientId""";

        List<Compte> comptes = new ArrayList<>();

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String accountNum = rs.getString("numCompte");
                BigDecimal solde = rs.getBigDecimal("solde");
                Date dateCreation = rs.getDate("dateCreation");
                String statut = rs.getString("statut");
                int clientID = rs.getInt("clientId");

                // Create Client
                Client client = new Client();
                client.setClientId(clientID);
                client.setNom(rs.getString("nom"));
                client.setPrenom(rs.getString("prenom"));
                client.setEmail(rs.getString("email"));
                client.setTel(rs.getString("tel"));

                // Create Compte and set client
                Compte c = new Compte(accountNum, solde, dateCreation, statut, clientID);
                c.setClient(client);

                comptes.add(c);
            }
        }

        return comptes;
    }

    public List<Compte> getComptes(String nom) throws SQLException {
        String query = """
        SELECT c.numCompte, c.solde, c.dateCreation, c.statut, c.clientId,
               cl.nom, cl.prenom, cl.email, cl.tel
        FROM Compte c
        INNER JOIN Client cl ON c.clientId = cl.clientId
        WHERE cl.nom LIKE ?
    """;

        List<Compte> comptes = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + nom + "%"); // To match partial names

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String accountNum = rs.getString("numCompte");
                    BigDecimal solde = rs.getBigDecimal("solde");
                    Date dateCreation = rs.getDate("dateCreation");
                    String statut = rs.getString("statut");
                    int clientID = rs.getInt("clientId");

                    // Create Client
                    Client client = new Client();
                    client.setClientId(clientID);
                    client.setNom(rs.getString("nom"));
                    client.setPrenom(rs.getString("prenom"));
                    client.setEmail(rs.getString("email"));
                    client.setTel(rs.getString("tel"));

                    // Create Compte and set client
                    Compte c = new Compte(accountNum, solde, dateCreation, statut, clientID);
                    c.setClient(client);

                    comptes.add(c);
                }
            }
        }

        return comptes;
    }

    // Method to update the account balance
    public boolean updateSolde(String numCompte, BigDecimal solde) throws SQLException {
        String query = "UPDATE Compte SET solde = ? WHERE numCompte = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBigDecimal(1, solde);
            stmt.setString(2, numCompte);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Method to check if the account exists
    public boolean compteExiste(String numCompte) throws SQLException {
        if (numCompte == null || numCompte.trim().isEmpty()) {
            return false;
        }

        String query = "SELECT COUNT(*) FROM Compte WHERE numCompte = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, numCompte);
            // Execute query after setting parameters
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // Method to transfer between accounts
    public boolean updateSolde(String numCompte, BigDecimal montant, boolean isDebit) throws SQLException {
        String sql = isDebit
                ? "UPDATE Compte SET solde = solde - ? WHERE numCompte = ? AND solde >= ?"
                : "UPDATE Compte SET solde = solde + ? WHERE numCompte = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Parameter binding
            stmt.setBigDecimal(1, montant);
            stmt.setString(2, numCompte);
            if (isDebit) {
                stmt.setBigDecimal(3, montant);
            }
            return stmt.executeUpdate() > 0; // No transaction management here
        }
    }

    public boolean transferSolde(String sourceCompte, String destCompte, BigDecimal montant) throws SQLException {
        try {
            connection.setAutoCommit(false); // 1. Start transaction
            boolean sourceUpdated = updateSolde(sourceCompte, montant, true);
            if (!sourceUpdated) {
                connection.rollback();
                return false;
            }
            boolean destUpdated = updateSolde(destCompte, montant, false);
            if (!destUpdated) {
                connection.rollback();
                return false;
            }
            connection.commit(); // 2. Commit transaction
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true); // 3. Reset auto-commit
        }
    }

}
