package com.quizBuilder.project.Entity;

import com.quizBuilder.project.Entity.Enum.Difficulty;
import com.quizBuilder.project.Entity.Enum.Topic;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String text;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Topic topic;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Option> options;

    @ManyToOne
    @JoinColumn(name = "quiz_code")
    private Quiz quiz;

}
