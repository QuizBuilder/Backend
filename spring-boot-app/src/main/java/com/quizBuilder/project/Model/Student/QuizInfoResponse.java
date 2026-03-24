package com.quizBuilder.project.Model.Student;

import com.quizBuilder.project.Entity.Enum.Difficulty;
import com.quizBuilder.project.Entity.Enum.Topic;
import com.quizBuilder.project.Model.Teacher.QuestionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizInfoResponse {
    private String code;

    private Instant startTime;

    private Instant endTime;

    private List<QuestionInfoResponse> questionList;

    private Topic topic;

    private Difficulty difficulty;

    private Long noOfQuestions;

}
