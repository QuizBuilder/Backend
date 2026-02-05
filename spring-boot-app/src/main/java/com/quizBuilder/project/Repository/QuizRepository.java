package com.quizBuilder.project.Repository;

import com.quizBuilder.project.Entity.Quiz;
import com.quizBuilder.project.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, String> {
    Optional<Quiz> findByCode(String code);

    List<Quiz> findByUser(User user);


}
