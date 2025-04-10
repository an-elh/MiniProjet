package dao;

import entite.Transaction;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    private Connection connection;

    // Constructor to initialize the connection
    public TransactionDAO() {
        this.connection = DbSingleton.getInstance().getConnection();
    }

    // Method to insert a new transaction into the database
    public boolean createTransaction(Transaction transaction) throws SQLException {
        String query = "INSERT INTO Transaction (type, montant, dateTransaction, numCompte) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, transaction.getType());
            stmt.setBigDecimal(2, transaction.getMontant());
            stmt.setTimestamp(3, new Timestamp(transaction.getDateTransaction().getTime()));
            stmt.setString(4, transaction.getNumCompte());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            // Rethrow the exception so the controller can handle it
            throw new SQLException("Error inserting transaction", e);
        }
    }

    public List<Transaction> getTransactions(String numCompte) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM Transaction WHERE numCompte = ? ORDER BY dateTransaction DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, numCompte);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int transactionId = rs.getInt("transactionId");
                String type = rs.getString("type");
                BigDecimal montant = rs.getBigDecimal("montant");
                Timestamp dateTransaction = rs.getTimestamp("dateTransaction");
                String accountNum = rs.getString("numCompte");

                // Create Transaction object and add it to the list
                Transaction transaction = new Transaction(type, montant, new Date(dateTransaction.getTime()), accountNum);
                transaction.setTransactionId(transactionId);
                transactions.add(transaction);
            }
            return transactions;
        } catch (SQLException e) {
            // Rethrow the exception so the controller can handle it
            throw new SQLException("Error retrieving transactions for account " + numCompte, e);
        }
    }
}
