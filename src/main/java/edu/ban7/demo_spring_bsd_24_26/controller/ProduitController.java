package edu.ban7.demo_spring_bsd_24_26.controller;

import edu.ban7.demo_spring_bsd_24_26.dao.ProduitDao;
import edu.ban7.demo_spring_bsd_24_26.model.Produit;
import edu.ban7.demo_spring_bsd_24_26.security.IsAdmin;
import edu.ban7.demo_spring_bsd_24_26.security.IsUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitDao produitDao;

    @GetMapping
    @IsAdmin
    public List<Produit> list() {
        return produitDao.findAll();
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<Produit> create(@RequestBody Produit produit) {
        produit.setId(null);
        return new ResponseEntity<>(produitDao.save(produit), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Produit> update(@PathVariable int id, @RequestBody Produit produit) {
        Optional<Produit> optionalProduit = produitDao.findById(id);

        if (optionalProduit.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        produit.setId(id);
        return new ResponseEntity<>(produitDao.save(produit), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (!produitDao.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        produitDao.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
