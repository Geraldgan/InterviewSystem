package com.interview.backend.support;

import java.util.List;

/**
 * 生成后的单道题。
 *
 * @param question 题目内容
 * @param answerIdea 答题思路
 * @param focusPoint 考察点
 * @param tags 标签列表
 * @param difficulty 难度
 */
public record GeneratedQuestionItem(
    String question,
    String answerIdea,
    String focusPoint,
    List<String> tags,
    String difficulty
) {
}
