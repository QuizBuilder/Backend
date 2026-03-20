package com.quizBuilder.project.Model.Student;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuestionInfoResponse {
    private String questionText;

    private String optAText;

    private String optBText;

    private String optCText;

    private String optDText;

    private String correctOptText;

    private String selectedOptText;
}
