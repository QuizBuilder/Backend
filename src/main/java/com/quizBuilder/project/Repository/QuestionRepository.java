package com.quizBuilder.project.Repository;

import com.quizBuilder.project.Entity.Enum.Difficulty;
import com.quizBuilder.project.Entity.Enum.Topic;
import com.quizBuilder.project.Entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Optional<Question> findById(Long id);

    @Query(value = "SELECT q.id FROM Question q WHERE q.topic=:topic AND q.difficulty=:difficulty")
    List<Long> findAllIdsByTopicAndDifficulty(@Param("topic") Topic topic, @Param("difficulty") Difficulty difficulty);

}
