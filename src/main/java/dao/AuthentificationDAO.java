package dao;

import entite.Authentification;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthentificationDAO {

    private String nom;
    private String motDePasse;

    public AuthentificationDAO(String nom, String motDePasse) {
        this.nom = nom;
        this.motDePasse = motDePasse;
    }

    public AuthentificationDAO() {
    }

    // Create a new user
    public boolean createUser(String nom, String motDePasse, int clientId) throws SQLException {
        String query = "INSERT INTO Authentification (nom, motDePasse, clientId) VALUES (?, ?, ?)";
        try (Connection conn = DbSingleton.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nom);
            stmt.setString(2, motDePasse);
            stmt.setInt(3, clientId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new SQLException("Error creating user: " + nom, e);
        }
    }

    // Delete a user by username (nom)
    
    

    // Throw SQLException for error handling by the controller
    public String getPasswordByUsername(String nom) throws SQLException {
        String query = "SELECT motDePasse FROM Authentification WHERE nom = ?";
        try (Connection conn = DbSingleton.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("motDePasse");
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching password for user: " + nom, e);
        }
        return null;
    }

    // Throw SQLException for error handling by the controller
    public boolean doesUsernameExist(String nom) throws SQLException {
        String query = "SELECT 1 FROM Authentification WHERE nom = ?";
        try (Connection conn = DbSingleton.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // If a result is returned, the username exists
        } catch (SQLException e) {
            throw new SQLException("Error checking if username exists: " + nom, e);
        }
    }

    // Throw SQLException for error handling by the controller
    public String[] seConnecter(String nom, String motDePasse) throws SQLException {
        String sql = "SELECT motDePasse, clientId, role FROM Authentification WHERE nom = ?";

        try (Connection con = DbSingleton.getInstance().getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("motDePasse");
                if (storedPassword.equals(motDePasse)) {
                    String[] result = new String[2];
                    result[0] = String.valueOf(rs.getInt("clientId"));
                    result[1] = rs.getString("role");
                    return result;
                }
            }

        } catch (SQLException e) {
            throw new SQLException("Error during login for user: " + nom, e);
        }

        return null; // return null on failure
    }

    public String getRole(String nom) throws SQLException {
        String query = "SELECT role FROM Authentification WHERE nom = ?";
        try (Connection conn = DbSingleton.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("role");
            }
            return "user";

        } catch (SQLException e) {
            throw new SQLException("Error fetching role for user: " + nom, e);
        }
    }

    public Authentification getAuthentificationById(int clientId) throws SQLException {
        String query = "SELECT * FROM Authentification WHERE clientId = ?";
        try (Connection con = DbSingleton.getInstance().getConnection(); PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String user=rs.getString("nom");
                String mdp = rs.getString("motDePasse");
                String role = rs.getString("role");
                return new Authentification(user,mdp,role);
            }
        }catch (SQLException e) {
            throw new SQLException(e);
        }
        return null;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
}
