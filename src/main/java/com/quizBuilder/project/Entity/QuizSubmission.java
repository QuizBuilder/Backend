package com.quizBuilder.project.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_submission")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long score;

    private Long quizRank;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "quiz_code")
    private Quiz quiz;
}
