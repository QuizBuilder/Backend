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
public class GenQuizInfoResponse {
    private String code;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private List<QuestionResponse> questionList;

    private Topic topic;

    private Difficulty difficulty;

    private Long noOfQuestions;
}
