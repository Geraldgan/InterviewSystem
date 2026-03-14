package com.interview.backend.service;

import com.interview.backend.config.AiFeatureProperties;
import com.interview.backend.config.OpenAiProperties;
import com.interview.backend.exception.AiGenerationException;
import com.interview.backend.support.GenerationCommand;
import com.interview.backend.support.GeneratedInterviewContent;
import org.springframework.stereotype.Service;

/**
 * 统一封装题目生成策略。
 */
@Service
public class QuestionGenerationService {

    private final OpenAiQuestionGenerator openAiQuestionGenerator;
    private final LocalMockInterviewGenerator localMockInterviewGenerator;
    private final OpenAiProperties openAiProperties;
    private final AiFeatureProperties aiFeatureProperties;

    public QuestionGenerationService(
        OpenAiQuestionGenerator openAiQuestionGenerator,
        LocalMockInterviewGenerator localMockInterviewGenerator,
        OpenAiProperties openAiProperties,
        AiFeatureProperties aiFeatureProperties
    ) {
        this.openAiQuestionGenerator = openAiQuestionGenerator;
        this.localMockInterviewGenerator = localMockInterviewGenerator;
        this.openAiProperties = openAiProperties;
        this.aiFeatureProperties = aiFeatureProperties;
    }

    /**
     * 根据当前配置选择真实 AI 或本地模拟生成。
     *
     * @param command 生成命令
     * @return 统一格式的题集结果
     */
    public GeneratedInterviewContent generate(GenerationCommand command) {
        if (!openAiProperties.hasApiKey()) {
            if (aiFeatureProperties.isMockEnabled()) {
                return localMockInterviewGenerator.generate(command);
            }
            throw new AiGenerationException("未配置 OPENAI_API_KEY，且当前未开启本地模拟生成。");
        }

        try {
            return openAiQuestionGenerator.generate(command);
        } catch (AiGenerationException exception) {
            if (aiFeatureProperties.isMockEnabled()) {
                return localMockInterviewGenerator.generate(command);
            }
            throw exception;
        }
    }
}
