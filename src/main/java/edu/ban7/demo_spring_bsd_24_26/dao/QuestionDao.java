package edu.ban7.demo_spring_bsd_24_26.dao;

import edu.ban7.demo_spring_bsd_24_26.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionDao extends JpaRepository<Question, Integer> {
    List<Question> findBySessionId(Integer sessionId);
}
