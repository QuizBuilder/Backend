package com.quizBuilder.project.Model.Teacher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponse {
    private String questionText;

    private String optAText;

    private String optBText;

    private String optCText;

    private String optDText;

    private String correctOptText;
}
