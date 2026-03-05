package edu.ban7.demo_spring_bsd_24_26.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.ban7.demo_spring_bsd_24_26.dao.AppUserDao;
import edu.ban7.demo_spring_bsd_24_26.model.AppUser;
import edu.ban7.demo_spring_bsd_24_26.security.IsAdmin;
import edu.ban7.demo_spring_bsd_24_26.view.AppUserView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/user")
@IsAdmin
public class AppUserController {

    @Autowired
    AppUserDao appUserDao;


    @GetMapping("/list")
    List<AppUser> getAll() {
        return appUserDao.findAll();
    }

    @GetMapping("/{id}")
    @JsonView(AppUserView.class)
    ResponseEntity<AppUser> get(@PathVariable int id) {

        Optional<AppUser> optionalAppUser = appUserDao.findById(id);

        //si le appUser n'existe pas
        if(optionalAppUser.isEmpty()) {
            //return ResponseEntity.notFound();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //return ResponseEntity.ok(optionalAppUser.get());
        return new ResponseEntity<>(optionalAppUser.get(), HttpStatus.OK);

    }

    @PostMapping
    ResponseEntity<AppUser> create(@RequestBody AppUser nouveauAppUser) {
        
        appUserDao.save(nouveauAppUser);
        
        return new ResponseEntity<>(nouveauAppUser, HttpStatus.CREATED);
        
    }
    
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable int id) {
        
        Optional<AppUser> optionalAppUser = appUserDao.findById(id);

        //si le appUser n'existe pas
        if(optionalAppUser.isEmpty()) {
            //return ResponseEntity.notFound();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        appUserDao.deleteById(id);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(
            @RequestBody AppUser appUser, 
            @PathVariable int id) {
        
        //on force l'id du appUser à modifier à être l'id récupéré dans l'url
        appUser.setId(id);

        Optional<AppUser> optionalAppUser = appUserDao.findById(id);

        //si le appUser n'existe pas
        if(optionalAppUser.isEmpty()) {
            //return ResponseEntity.notFound();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        appUserDao.save(appUser);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        
    }

}
