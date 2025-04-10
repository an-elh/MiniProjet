package entite;
public class Authentification {

    private String nom;
    private String motDePasse;
    private String role;
    private int clientId;
    public Authentification(String nom, String motDePasse, String role) {
        this.nom = nom;
        this.motDePasse = motDePasse;
        this.role=role;
    }

    public Authentification() {
    }

    public boolean seConnecter(String nom, String motDePasse) {
        // Validate credentials against stored values
        return this.nom.equals(nom) && this.motDePasse.equals(motDePasse);
    }
    public String getRole() {
       return role;
    }

    public String getNom() {
        return nom;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
    
}
