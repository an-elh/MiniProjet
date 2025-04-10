package entite;

import java.math.BigDecimal;
import java.util.Date;

public class Transaction {

    private int transactionId;
    private String type;
    private BigDecimal montant;
    private Date dateTransaction;
    private String numCompte;

    public Transaction(String type, BigDecimal montant, Date dateTransaction, String numCompte) {
        this.type = type;
        this.montant = montant;
        this.dateTransaction = dateTransaction;
        this.numCompte = numCompte;
    }

    // Getter and Setter methods for each field
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public Date getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(Date dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    public String getNumCompte() {
        return numCompte;
    }

    public void setNumCompte(String numCompte) {
        this.numCompte = numCompte;
    }
}