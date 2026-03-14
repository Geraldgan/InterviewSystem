package com.interview.backend.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.backend.config.OpenAiProperties;
import com.interview.backend.entity.GenerationSource;
import com.interview.backend.exception.AiGenerationException;
import com.interview.backend.support.GenerationCommand;
import com.interview.backend.support.GeneratedInterviewContent;
import com.interview.backend.support.GeneratedQuestionItem;
import com.interview.backend.support.InterviewPromptBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

/**
 * 调用 OpenAI Responses API 生成结构化面试题。
 */
@Component
public class OpenAiQuestionGenerator {

    private final RestClient openAiRestClient;
    private final OpenAiProperties openAiProperties;
    private final ObjectMapper objectMapper;
    private final InterviewPromptBuilder promptBuilder;

    public OpenAiQuestionGenerator(
        RestClient openAiRestClient,
        OpenAiProperties openAiProperties,
        ObjectMapper objectMapper,
        InterviewPromptBuilder promptBuilder
    ) {
        this.openAiRestClient = openAiRestClient;
        this.openAiProperties = openAiProperties;
        this.objectMapper = objectMapper;
        this.promptBuilder = promptBuilder;
    }

    /**
     * 使用 OpenAI 生成题目。
     *
     * @param command 生成命令
     * @return 结构化题集结果
     */
    public GeneratedInterviewContent generate(GenerationCommand command) {
        if (!openAiProperties.hasApiKey()) {
            throw new AiGenerationException("未配置 OPENAI_API_KEY，无法调用 OpenAI。");
        }

        String prompt = promptBuilder.buildPrompt(command);
        Map<String, Object> requestBody = buildRequestBody(prompt);

        try {
            String rawResponse = openAiRestClient.post()
                .uri("/responses")
                .headers(headers -> headers.setBearerAuth(openAiProperties.getApiKey()))
                .body(requestBody)
                .retrieve()
                .body(String.class);

            OpenAiResponseEnvelope envelope = objectMapper.readValue(rawResponse, OpenAiResponseEnvelope.class);
            String structuredJson = extractStructuredJson(envelope);
            StructuredInterviewResult result = objectMapper.readValue(structuredJson, StructuredInterviewResult.class);

            if (result.questions() == null || result.questions().isEmpty()) {
                throw new AiGenerationException("OpenAI 已返回响应，但没有生成任何题目。");
            }

            List<GeneratedQuestionItem> questions = result.questions().stream()
                .map(item -> new GeneratedQuestionItem(
                    item.question(),
                    item.answerIdea(),
                    item.focusPoint(),
                    item.tags() == null || item.tags().isEmpty() ? List.of("AI生成", command.positionTitle()) : item.tags(),
                    StringUtils.hasText(item.difficulty()) ? item.difficulty() : command.difficulty()
                ))
                .toList();

            return new GeneratedInterviewContent(
                StringUtils.hasText(result.summary()) ? result.summary() : command.positionTitle() + " 面试题集",
                questions,
                GenerationSource.OPENAI,
                StringUtils.hasText(envelope.model()) ? envelope.model() : openAiProperties.getModel(),
                prompt,
                rawResponse
            );
        } catch (AiGenerationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new AiGenerationException("调用 OpenAI 生成题目失败: " + exception.getMessage(), exception);
        }
    }

    private Map<String, Object> buildRequestBody(String prompt) {
        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", openAiProperties.getModel());
        requestBody.put("input", prompt);
        requestBody.put("store", false);
        requestBody.put("reasoning", Map.of("effort", openAiProperties.getReasoningEffort()));
        requestBody.put("text", Map.of(
            "format", Map.of(
                "type", "json_schema",
                "name", "interview_question_set",
                "strict", true,
                "schema", promptBuilder.buildSchema()
            )
        ));
        return requestBody;
    }

    /**
     * Responses API 返回的结构较灵活，因此这里做一层兼容提取。
     */
    private String extractStructuredJson(OpenAiResponseEnvelope envelope) {
        if (StringUtils.hasText(envelope.outputText())) {
            return envelope.outputText();
        }

        if (envelope.output() == null) {
            throw new AiGenerationException("OpenAI 返回结果中缺少 output 字段。");
        }

        return envelope.output().stream()
            .filter(item -> item.content() != null)
            .flatMap(item -> item.content().stream())
            .map(OutputContent::text)
            .filter(StringUtils::hasText)
            .findFirst()
            .orElseThrow(() -> new AiGenerationException("无法从 OpenAI 响应中提取结构化 JSON。"));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OpenAiResponseEnvelope(
        String model,
        @JsonProperty("output_text") String outputText,
        List<OutputItem> output
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OutputItem(
        List<OutputContent> content
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OutputContent(
        String type,
        String text
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record StructuredInterviewResult(
        String summary,
        List<StructuredQuestionItem> questions
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record StructuredQuestionItem(
        String question,
        String answerIdea,
        String focusPoint,
        String difficulty,
        List<String> tags
    ) {
    }
}
