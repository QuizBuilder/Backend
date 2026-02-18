package com.quizBuilder.project.Model.AI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIQuizRequest {
    private String topic;
    private String difficulty;
    private Long noOfQuestions;
    private String additionalInstruction;
}
