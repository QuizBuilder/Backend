package com.quizBuilder.project.Model.Teacher;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.quizBuilder.project.Entity.Enum.Difficulty;
import com.quizBuilder.project.Entity.Enum.Topic;
import com.quizBuilder.project.Entity.Quiz;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenQuizzesResponse {
    private String quizCode;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime endTime;
    private Topic topic;
    private Difficulty difficulty;
}
