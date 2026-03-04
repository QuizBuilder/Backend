package com.quizBuilder.project.Model.Teacher;

import com.quizBuilder.project.Entity.Enum.Difficulty;
import com.quizBuilder.project.Entity.Enum.Topic;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizGenerationRequest {
    @NotNull(message = "NoOfQues is required")
    private Long noOfQuestions;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    @NotNull(message = "Topic is required")
    private Topic topic;

    @NotNull(message = "startTime is required")
    private LocalDateTime startTime;

    @NotNull(message = "endTime is required")
    private LocalDateTime endTime;

    private String additionalInstruction;
}
