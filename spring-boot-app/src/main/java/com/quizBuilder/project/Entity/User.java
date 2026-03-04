package com.quizBuilder.project.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quizBuilder.project.Entity.Enum.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Quiz> quizList = new ArrayList<>();  //generation for teacher

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<QuizSubmission> quizSubmissionList = new ArrayList<>();

    @ManyToMany(mappedBy = "userList", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Quiz> quizzesAttempted = new ArrayList<>();

}
