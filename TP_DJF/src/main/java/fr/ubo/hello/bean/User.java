package fr.ubo.hello.bean;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String password;
    @JsonIgnore
    private LocalDateTime dateCreation;
    @JsonIgnore
    private LocalDateTime dateModification;

    public User() {}

    public User(String nom, String prenom, String email, String telephone, String password) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.password = password;
    }

    public User(int id, String nom, String prenom, String email, String telephone, String password) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.password = password;
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }
}