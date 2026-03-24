package com.quizBuilder.project.Model.Teacher;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.quizBuilder.project.Entity.Enum.Difficulty;
import com.quizBuilder.project.Entity.Enum.Topic;
import com.quizBuilder.project.Entity.Quiz;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenQuizzesResponse {
    private String quizCode;
    private Instant startTime;
    private Instant endTime;
    private Topic topic;
    private Difficulty difficulty;
}
