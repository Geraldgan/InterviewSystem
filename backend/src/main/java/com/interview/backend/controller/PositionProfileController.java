package com.interview.backend.controller;

import java.util.List;

import com.interview.backend.dto.PositionProfileResponse;
import com.interview.backend.service.PositionProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 岗位画像接口。
 */
@RestController
@RequestMapping("/api/positions")
public class PositionProfileController {

    private final PositionProfileService positionProfileService;

    public PositionProfileController(PositionProfileService positionProfileService) {
        this.positionProfileService = positionProfileService;
    }

    /**
     * 获取系统预置的岗位列表。
     *
     * @return 岗位画像列表
     */
    @GetMapping
    public List<PositionProfileResponse> listProfiles() {
        return positionProfileService.listAvailableProfiles();
    }
}
