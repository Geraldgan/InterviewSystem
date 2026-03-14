package com.interview.backend.repository;

import java.util.List;
import java.util.Optional;

import com.interview.backend.entity.QuestionSet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 题集仓储。
 */
public interface QuestionSetRepository extends JpaRepository<QuestionSet, Long> {

    List<QuestionSet> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = "questions")
    Optional<QuestionSet> findById(Long id);
}
