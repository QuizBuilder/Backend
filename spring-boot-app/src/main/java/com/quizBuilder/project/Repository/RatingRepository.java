package com.quizBuilder.project.Repository;

import com.quizBuilder.project.Entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
