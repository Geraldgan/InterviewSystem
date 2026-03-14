package com.interview.backend.dto;

import java.time.LocalDateTime;

/**
 * 题集列表摘要响应。
 *
 * @param id 题集 ID
 * @param positionTitle 岗位名称
 * @param difficulty 难度
 * @param questionCount 题目数量
 * @param source 数据来源
 * @param aiModel 生成模型
 * @param createdAt 创建时间
 */
public record QuestionSetSummaryResponse(
    Long id,
    String positionTitle,
    String difficulty,
    Integer questionCount,
    String source,
    String aiModel,
    LocalDateTime createdAt
) {
}
