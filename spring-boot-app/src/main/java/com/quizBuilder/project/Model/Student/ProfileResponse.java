package com.quizBuilder.project.Model.Student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {

    String name;

    String emailId;

    String role;

    Double rating;

    Long totalQs;

    Long correctAns;

    String level;

    Double accuracy;

    List<Double> prevRatings;
}
