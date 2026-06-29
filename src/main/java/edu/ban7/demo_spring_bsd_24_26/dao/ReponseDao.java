package edu.ban7.demo_spring_bsd_24_26.dao;

import edu.ban7.demo_spring_bsd_24_26.model.Reponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReponseDao extends JpaRepository<Reponse, Integer> {
    List<Reponse> findByQuestionSessionId(Integer sessionId);

    List<Reponse> findByQuestionSessionIdAndAppUserId(Integer sessionId, Integer appUserId);

    Optional<Reponse> findByQuestionIdAndAppUserId(Integer questionId, Integer appUserId);
}
