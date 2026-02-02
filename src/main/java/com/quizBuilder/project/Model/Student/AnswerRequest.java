package com.quizBuilder.project.Model.Student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerRequest {
    private Long questionId;

    private Long selectedOptionId;
}
