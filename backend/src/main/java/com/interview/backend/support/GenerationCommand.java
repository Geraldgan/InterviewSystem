package com.interview.backend.support;

import java.util.List;

/**
 * 统一的题目生成命令对象，便于在服务之间传递参数。
 *
 * @param positionCode 岗位编码
 * @param positionTitle 岗位名称
 * @param difficulty 难度
 * @param questionCount 题目数量
 * @param customRequirements 自定义要求
 * @param focusAreas 关注知识点
 * @param includeScenarioQuestions 是否包含场景题
 * @param locale 输出语言
 */
public record GenerationCommand(
    String positionCode,
    String positionTitle,
    String difficulty,
    Integer questionCount,
    String customRequirements,
    List<String> focusAreas,
    boolean includeScenarioQuestions,
    String locale
) {
}
