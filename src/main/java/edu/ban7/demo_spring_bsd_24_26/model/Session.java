package edu.ban7.demo_spring_bsd_24_26.model;

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

    @Column(name = "nombre_joueur")
    private int nombreJoueur;

    @OneToMany(mappedBy = "session")
    private List<Question> questions;

    // Constructeurs
    public Session() {}

    public Session(String nom, int nombreJoueur) {
        this.nom = nom;
        this.nombreJoueur = nombreJoueur;
}
