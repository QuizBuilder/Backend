package com.quizBuilder.project.Repository;

import com.quizBuilder.project.Entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionAnsRepository extends JpaRepository<SubmissionAnswer, Long> {
    public SubmissionAnswer findByQuestionAndQuizSubmission(Question question, QuizSubmission quizSubmission);



    List<SubmissionAnswer> findByQuizSubmission(QuizSubmission quizSubmission);
}
