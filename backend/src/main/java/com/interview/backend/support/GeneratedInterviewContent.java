package com.interview.backend.support;

import java.util.List;

import com.interview.backend.entity.GenerationSource;

/**
 * AI 或本地模拟生成后的统一结果。
 *
 * @param summary 本次题集的概述
 * @param questions 题目列表
 * @param source 结果来源
 * @param aiModel 使用的模型名称或模拟生成器名称
 * @param promptSnapshot 发给模型的提示词快照
 * @param rawResponse 模型返回原文，便于排查问题
 */
public record GeneratedInterviewContent(
    String summary,
    List<GeneratedQuestionItem> questions,
    GenerationSource source,
    String aiModel,
    String promptSnapshot,
    String rawResponse
) {
}
