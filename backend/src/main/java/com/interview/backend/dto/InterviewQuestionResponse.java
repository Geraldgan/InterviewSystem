package com.interview.backend.dto;

import java.util.List;

/**
 * 单道题目的接口响应。
 *
 * @param id 题目 ID
 * @param displayOrder 展示顺序
 * @param question 题目内容
 * @param answerIdea 答题思路
 * @param focusPoint 考察点
 * @param tags 标签列表
 * @param difficulty 难度
 */
public record InterviewQuestionResponse(
    Long id,
    Integer displayOrder,
    String question,
    String answerIdea,
    String focusPoint,
    List<String> tags,
    String difficulty
) {
}
