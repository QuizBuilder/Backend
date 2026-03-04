package com.quizBuilder.project.Entity;

import com.quizBuilder.project.Entity.Enum.Difficulty;
import com.quizBuilder.project.Entity.Enum.Topic;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {
    @Id
    private String code;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Topic topic;

    @Column(nullable = false)
    private Long noOfQuestions;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User user;

    @OneToMany(mappedBy = "quiz")
    private List<QuizSubmission> quizSubmissionList;


    @OneToMany(mappedBy = "quiz", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Question> questionList;

    @ManyToMany
    @JoinTable(
            name = "quiz_user",
            joinColumns = @JoinColumn(name = "quiz_code"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> userList;
}
