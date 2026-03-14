package com.interview.backend.repository;

import java.util.List;
import java.util.Optional;

import com.interview.backend.entity.PositionProfile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 岗位画像仓储。
 */
public interface PositionProfileRepository extends JpaRepository<PositionProfile, Long> {

    List<PositionProfile> findAllByActiveTrueOrderByTitleAsc();

    Optional<PositionProfile> findByCodeAndActiveTrue(String code);
}
