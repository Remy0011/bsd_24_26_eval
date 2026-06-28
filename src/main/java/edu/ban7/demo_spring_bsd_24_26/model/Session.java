package edu.ban7.demo_spring_bsd_24_26.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity


public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    protected String nom;

    protected int nombreJoueur;

    @JsonIgnore
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "session_joueur",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "app_user_id")
    )
    private List<AppUser> joueurs;

    // Constructeurs
    public Session() {}

    public Session(String nom, int nombreJoueur) {
        this.nom = nom;
        this.nombreJoueur = nombreJoueur;
    }
}
