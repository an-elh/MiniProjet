package controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import dao.CompteDAO;
import dao.TransactionDAO;
import entite.Compte;
import entite.Transaction;

public class CompteController {

    private CompteDAO compteDAO;
    private TransactionDAO transactionDAO;

    // Constructor initializes the DAOs
    public CompteController() {
        this.compteDAO = new CompteDAO();
        this.transactionDAO = new TransactionDAO();
    }
    public CompteController(CompteDAO compteDAO, TransactionDAO transactionDAO) {
        this.compteDAO = compteDAO;
        this.transactionDAO = transactionDAO;
    }
    // Method to create a new account
    public boolean createCompte(Compte compte) {
        try {
            return compteDAO.createCompte(compte);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to retrieve an account by its client id
    public Compte getCompte(int clientId) {
        try {
            System.out.println("Compte :"+compteDAO.getCompte(clientId));
            return compteDAO.getCompte(clientId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Compte getCompte(String numCompte) {
        try {
            System.out.println("Compte :"+compteDAO.getCompte(numCompte));
            return compteDAO.getCompte(numCompte);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Compte> getAllComptes() {
        try {
            return compteDAO.getComptes();   
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Compte> getAllComptesByName(String nom) {
        try {
            return compteDAO.getComptes(nom);   
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to update the balance of an account
    public boolean updateSolde(String numCompte, BigDecimal newSolde) {
        try {
            return compteDAO.updateSolde(numCompte, newSolde);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to list all transactions of a specific account
    public List<Transaction> getTransactionsByAccount(String numCompte) {
        try {
            return transactionDAO.getTransactions(numCompte);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to deposit money into an account
    public boolean deposit(String numCompte, BigDecimal montant) {
        try {
            Compte compte = compteDAO.getCompte(numCompte);
            if (compte != null) {
                BigDecimal currentSolde = compte.getSolde();
                BigDecimal newSolde = currentSolde.add(montant);
                boolean isUpdated = updateSolde(numCompte, newSolde);
                if (isUpdated) {
                    // Create a new transaction for the deposit
                    Transaction transaction = new Transaction("Deposit", montant, new java.util.Date(), numCompte);
                    transactionDAO.createTransaction(transaction);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to withdraw money from an account
    public boolean withdraw(String numCompte, BigDecimal montant) {
        try {
            Compte compte = compteDAO.getCompte(numCompte);
            if (compte != null && compte.getSolde().compareTo(montant) >= 0) {
                BigDecimal currentSolde = compte.getSolde();
                BigDecimal newSolde = currentSolde.subtract(montant);
                boolean isUpdated = updateSolde(numCompte, newSolde);
                if (isUpdated) {
                    // Create a new transaction for the withdrawal
                    Transaction transaction = new Transaction("Withdrawal", montant, new java.util.Date(), numCompte);
                    transactionDAO.createTransaction(transaction);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to get account balance
    public BigDecimal getSolde(String numCompte) {
        try {
            Compte compte = compteDAO.getCompte(numCompte);
            return compte != null ? compte.getSolde() : BigDecimal.ZERO;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    // methode to execute the transfer
    public boolean effectuerVirement(String senderNum, String receiverNum, BigDecimal montant) {
        try {
            // Input validation
            if (senderNum == null || receiverNum == null || montant == null) {
                System.out.println("Invalid parameters: null values not allowed");
                return false;
            }

            // Get both accounts first to verify they exist
            Compte sender = compteDAO.getCompte(senderNum);
            Compte receiver = compteDAO.getCompte(receiverNum);

            if (sender == null) {
                System.out.println("Sender account " + senderNum + " does not exist.");
                return false;
            }

            if (receiver == null) {
                System.out.println("Receiver account " + receiverNum + " does not exist.");
                return false;
            }

            // Check sufficient balance
            if (sender.getSolde().compareTo(montant) < 0) {
                System.out.println("Insufficient balance in sender's account.");
                return false;
            }

            // Perform transfer
            BigDecimal newSenderSolde = sender.getSolde().subtract(montant);
            BigDecimal newReceiverSolde = receiver.getSolde().add(montant);

            boolean senderUpdated = compteDAO.updateSolde(senderNum, newSenderSolde);
            boolean receiverUpdated = compteDAO.updateSolde(receiverNum, newReceiverSolde);

            if (senderUpdated && receiverUpdated) {
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Database error during transfer: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    
}
