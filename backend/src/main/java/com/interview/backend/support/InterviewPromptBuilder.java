package com.interview.backend.support;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

/**
 * 统一管理提示词与结构化输出 Schema。
 */
@Component
public class InterviewPromptBuilder {

    /**
     * 构建一段对模型更友好的生成提示词。
     *
     * @param command 生成命令
     * @return 最终提示词
     */
    public String buildPrompt(GenerationCommand command) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("你是一名资深技术面试官和面试辅导老师。");
        joiner.add("请根据给定岗位生成一套高质量中文面试题，并给出清晰、可执行的答题思路。");
        joiner.add("输出必须是 JSON，并严格符合提供的 schema。");
        joiner.add("要求：");
        joiner.add("1. 题目贴近真实面试，避免空泛。");
        joiner.add("2. 题目和答题思路都要体现岗位差异。");
        joiner.add("3. 答题思路采用结构化表达，突出面试官关注点。");
        joiner.add("4. 如果要求场景题，请至少覆盖 2 道场景分析题。");
        joiner.add("");
        joiner.add("岗位名称: " + command.positionTitle());
        joiner.add("岗位编码: " + safeValue(command.positionCode()));
        joiner.add("难度: " + command.difficulty());
        joiner.add("题目数量: " + command.questionCount());
        joiner.add("关注知识点: " + String.join("、", command.focusAreas()));
        joiner.add("自定义要求: " + safeValue(command.customRequirements()));
        joiner.add("输出语言: " + command.locale());
        joiner.add("是否包含场景题: " + (command.includeScenarioQuestions() ? "是" : "否"));
        return joiner.toString();
    }

    /**
     * 构建结构化输出 Schema，尽量让模型返回稳定 JSON。
     *
     * @return JSON Schema 对应的 Map
     */
    public Map<String, Object> buildSchema() {
        return Map.of(
            "type", "object",
            "additionalProperties", false,
            "properties", Map.of(
                "summary", Map.of(
                    "type", "string",
                    "description", "对这套题的整体说明"
                ),
                "questions", Map.of(
                    "type", "array",
                    "items", Map.of(
                        "type", "object",
                        "additionalProperties", false,
                        "properties", Map.of(
                            "question", Map.of("type", "string"),
                            "answerIdea", Map.of("type", "string"),
                            "focusPoint", Map.of("type", "string"),
                            "difficulty", Map.of("type", "string"),
                            "tags", Map.of(
                                "type", "array",
                                "items", Map.of("type", "string")
                            )
                        ),
                        "required", List.of("question", "answerIdea", "focusPoint", "difficulty", "tags")
                    )
                )
            ),
            "required", List.of("summary", "questions")
        );
    }

    private String safeValue(String value) {
        return value == null || value.isBlank() ? "无" : value;
    }
}
