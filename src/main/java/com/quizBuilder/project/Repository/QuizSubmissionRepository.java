package com.quizBuilder.project.Repository;

import com.quizBuilder.project.Entity.Quiz;
import com.quizBuilder.project.Entity.QuizSubmission;
import com.quizBuilder.project.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    QuizSubmission findByQuizAndUser(Quiz quiz, User user);
}
