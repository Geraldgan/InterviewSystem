package com.interview.backend.dto;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * 生成题目请求。
 *
 * @param positionCode 岗位编码，可直接命中系统预设岗位
 * @param positionTitle 岗位名称，支持自定义，如“AI Agent 工程师”
 * @param customRequirements 自定义要求，例如“重点考察 Swift 并发”
 * @param questionCount 需要生成的题目数量
 * @param difficulty 难度标记，如 JUNIOR、MID、SENIOR
 * @param focusAreas 关注的知识点列表
 * @param includeScenarioQuestions 是否包含场景题
 * @param locale 输出语言，例如 zh-CN
 */
public record GenerateQuestionSetRequest(
    String positionCode,
    String positionTitle,
    @Size(max = 600, message = "自定义要求不能超过 600 个字符")
    String customRequirements,
    @Min(value = 3, message = "至少生成 3 道题")
    @Max(value = 20, message = "最多生成 20 道题")
    Integer questionCount,
    String difficulty,
    List<String> focusAreas,
    Boolean includeScenarioQuestions,
    String locale
) {
}
