package com.quizBuilder.project.Model.AI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIQuestionResponse {
    private String question;
    private List<String> options;
    @JsonProperty("correct_index")
    private int correctIndex;
}
