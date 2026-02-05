package com.quizBuilder.project.Model.Student;

import com.quizBuilder.project.Entity.Enum.Difficulty;
import com.quizBuilder.project.Entity.Enum.Topic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentQuizHistoryResponse {
    private String quizCode;

    private Topic topic;

    private Difficulty difficulty;

    private Long score;

    private Long rank;
}
