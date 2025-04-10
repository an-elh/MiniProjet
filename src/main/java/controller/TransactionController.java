package controller;

import dao.TransactionDAO;
import entite.Transaction;
import java.math.BigDecimal;
import java.util.List;
import java.sql.SQLException;

public class TransactionController {

    public TransactionDAO transactionDAO;

    // Constructor to initialize the DAO
    public TransactionController() {
        this.transactionDAO = new TransactionDAO();
    }

    // Method to create a new transaction
    public boolean createTransaction(String type, BigDecimal montant, java.util.Date dateTransaction, String numCompte) {
        // Create a new transaction object
        Transaction transaction = new Transaction(type, montant, dateTransaction, numCompte);
        
        try {
            // Call the DAO method to insert the transaction into the database
            return transactionDAO.createTransaction(transaction);
        } catch (SQLException e) {
            // Handle the exception (log it or show an error message)
            e.printStackTrace();
            return false; // You can return false to indicate failure
        }
    }

    // Method to get all transactions for a given account
    public List<Transaction> getTransactions(String numCompte) {
        try {
            // Retrieve transactions from the DAO
            return transactionDAO.getTransactions(numCompte);
        } catch (SQLException e) {
            // Handle the exception (log it or show an error message)
            e.printStackTrace();
            return null; // Return null if there's an error retrieving transactions
        }
    }
}