package com.interview.backend.dto;

import java.util.List;

/**
 * 岗位画像响应。
 *
 * @param id 岗位 ID
 * @param code 岗位编码
 * @param title 岗位名称
 * @param category 岗位方向
 * @param level 级别
 * @param description 岗位说明
 * @param defaultFocusAreas 默认考察点
 */
public record PositionProfileResponse(
    Long id,
    String code,
    String title,
    String category,
    String level,
    String description,
    List<String> defaultFocusAreas
) {
}
