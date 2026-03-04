package com.quizBuilder.project.Repository;

import com.quizBuilder.project.Entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long> {
}
