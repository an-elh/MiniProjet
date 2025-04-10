package entite;

public class Client {

    private int clientId;
    private String nom;
    private String prenom;
    private String email;
    private String tel;
    private Authentification auth;
    private Compte compte;

    public Client(String nom, String prenom, String email, String tel) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.tel = tel;
    }

    public Client() {
    }

    // Getters and Setters
    public int getClientId() {
        return clientId;
    }

    public Authentification getAuth() {
        return auth;
    }

    public void setAuth(Authentification auth) {
        this.auth = auth;
    }

    public Compte getCompte() {
        return compte;
    }

    public void setCompte(Compte compte) {
        this.compte = compte;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

}
