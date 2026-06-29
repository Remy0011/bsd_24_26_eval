package edu.ban7.demo_spring_bsd_24_26.dao;

import edu.ban7.demo_spring_bsd_24_26.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProduitDao extends JpaRepository<Produit, Integer> {
}
