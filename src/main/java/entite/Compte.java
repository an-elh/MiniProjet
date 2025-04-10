package entite;

import java.math.BigDecimal;
import java.sql.Date;

public class Compte {

    private String numCompte; 
    private BigDecimal solde; 
    private Date dateCreation; 
    private String statut;
    private int clientId;
    private Client client;

    public Compte() {
    }
    
    public Compte(String numCompte, BigDecimal solde, Date dateCreation, String statut, int clientId) {
        this.numCompte = numCompte;
        this.solde = solde;
        this.dateCreation = dateCreation;
        this.statut = statut;
        this.clientId = clientId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getNumCompte() {
        return numCompte;
    }

    public void setNumCompte(String numCompte) {
        this.numCompte = numCompte;
    }

    public BigDecimal getSolde() {
        return solde;
    }

    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    // Method to deposit money into the account
    public void deposerArgent(BigDecimal montant) {
        if (montant.compareTo(BigDecimal.ZERO) > 0) {
            this.solde = this.solde.add(montant);
        } else {
            System.out.println("Montant invalide.");
        }
    }

    // Method to withdraw money from the account
    public void retirerArgent(BigDecimal montant) {
        if (montant.compareTo(BigDecimal.ZERO) > 0 && this.solde.compareTo(montant) >= 0) {
            this.solde = this.solde.subtract(montant);
        } else {
            System.out.println("Montant invalide ou solde insuffisant.");
        }
    }

    // Method to check the balance of the account
    public BigDecimal consulterSolde() {
        return this.solde;
    }

    @Override
    public String toString() {
        return "Compte [numCompte=" + numCompte + ", solde=" + solde + ", dateCreation=" + dateCreation + ", statut=" + statut + "]";
    }

}
