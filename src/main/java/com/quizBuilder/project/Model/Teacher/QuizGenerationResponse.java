package com.quizBuilder.project.Model.Teacher;

import com.quizBuilder.project.Entity.Enum.Difficulty;
import com.quizBuilder.project.Entity.Enum.Topic;
import com.quizBuilder.project.Entity.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizGenerationResponse {
    private String code;

    private Long noOfQuestions;

    private Difficulty difficulty;

    private Topic topic;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    List<QuestionResponse> questionList;
}
