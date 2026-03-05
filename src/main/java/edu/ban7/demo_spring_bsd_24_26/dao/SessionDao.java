package edu.ban7.demo_spring_bsd_24_26.dao;


import edu.ban7.demo_spring_bsd_24_26.model.AppUser;
import edu.ban7.demo_spring_bsd_24_26.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionDao extends JpaRepository<AppUser, Integer> {

    Optional<Session> findByName(String nom);
}