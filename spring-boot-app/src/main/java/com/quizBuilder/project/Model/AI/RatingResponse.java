package com.quizBuilder.project.Model.AI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingResponse {
    Double theta;
    Double rating;
    String level;
    Long correct_answers;
    Long total_questions;
    Double accuracy;
}
