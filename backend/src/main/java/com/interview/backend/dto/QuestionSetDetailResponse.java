package com.interview.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 题集详情响应。
 *
 * @param id 题集 ID
 * @param positionCode 岗位编码
 * @param positionTitle 岗位名称
 * @param difficulty 难度
 * @param questionCount 题目数量
 * @param customRequirements 自定义要求
 * @param summary AI 生成摘要
 * @param source 数据来源
 * @param aiModel 使用的模型或生成器名称
 * @param createdAt 创建时间
 * @param questions 题目列表
 */
public record QuestionSetDetailResponse(
    Long id,
    String positionCode,
    String positionTitle,
    String difficulty,
    Integer questionCount,
    String customRequirements,
    String summary,
    String source,
    String aiModel,
    LocalDateTime createdAt,
    List<InterviewQuestionResponse> questions
) {
}
