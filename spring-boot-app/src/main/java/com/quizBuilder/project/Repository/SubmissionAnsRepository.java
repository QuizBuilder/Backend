package com.quizBuilder.project.Repository;

import com.quizBuilder.project.Entity.Option;
import com.quizBuilder.project.Entity.Question;
import com.quizBuilder.project.Entity.QuizSubmission;
import com.quizBuilder.project.Entity.SubmissionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmissionAnsRepository extends JpaRepository<SubmissionAnswer, Long> {
    public SubmissionAnswer findByQuestionAndQuizSubmission(Question question, QuizSubmission quizSubmission);
}
