package com.interview.backend.service;

import java.util.Arrays;
import java.util.List;

import com.interview.backend.dto.PositionProfileResponse;
import com.interview.backend.entity.PositionProfile;
import com.interview.backend.repository.PositionProfileRepository;
import org.springframework.stereotype.Service;

/**
 * 岗位画像查询服务。
 */
@Service
public class PositionProfileService {

    private final PositionProfileRepository positionProfileRepository;

    public PositionProfileService(PositionProfileRepository positionProfileRepository) {
        this.positionProfileRepository = positionProfileRepository;
    }

    /**
     * 查询所有启用中的岗位画像。
     *
     * @return 前端可直接展示的岗位列表
     */
    public List<PositionProfileResponse> listAvailableProfiles() {
        return positionProfileRepository.findAllByActiveTrueOrderByTitleAsc()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private PositionProfileResponse toResponse(PositionProfile profile) {
        return new PositionProfileResponse(
            profile.getId(),
            profile.getCode(),
            profile.getTitle(),
            profile.getCategory(),
            profile.getLevel(),
            profile.getDescription(),
            splitTags(profile.getDefaultFocusAreas())
        );
    }

    private List<String> splitTags(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return List.of();
        }
        return Arrays.stream(rawValue.split(","))
            .map(String::trim)
            .filter(value -> !value.isBlank())
            .toList();
    }
}
