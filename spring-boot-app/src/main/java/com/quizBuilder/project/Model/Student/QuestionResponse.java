package com.quizBuilder.project.Model.Student;

import com.quizBuilder.project.Entity.Option;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponse {
    private Long questionId;
    private Long optAId;
    private Long optBId;
    private Long optCId;
    private Long optDId;

    private String questionText;

    private String optAText;

    private String optBText;

    private String optCText;

    private String optDText;
}
