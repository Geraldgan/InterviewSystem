package com.interview.backend.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.interview.backend.dto.GenerateQuestionSetRequest;
import com.interview.backend.dto.InterviewQuestionResponse;
import com.interview.backend.dto.QuestionSetDetailResponse;
import com.interview.backend.dto.QuestionSetSummaryResponse;
import com.interview.backend.entity.GenerationStatus;
import com.interview.backend.entity.InterviewQuestion;
import com.interview.backend.entity.PositionProfile;
import com.interview.backend.entity.QuestionSet;
import com.interview.backend.exception.ResourceNotFoundException;
import com.interview.backend.repository.PositionProfileRepository;
import com.interview.backend.repository.QuestionSetRepository;
import com.interview.backend.support.GenerationCommand;
import com.interview.backend.support.GeneratedInterviewContent;
import com.interview.backend.support.GeneratedQuestionItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 题集创建与查询服务。
 */
@Service
public class QuestionSetService {

    private final PositionProfileRepository positionProfileRepository;
    private final QuestionSetRepository questionSetRepository;
    private final QuestionGenerationService questionGenerationService;

    public QuestionSetService(
        PositionProfileRepository positionProfileRepository,
        QuestionSetRepository questionSetRepository,
        QuestionGenerationService questionGenerationService
    ) {
        this.positionProfileRepository = positionProfileRepository;
        this.questionSetRepository = questionSetRepository;
        this.questionGenerationService = questionGenerationService;
    }

    /**
     * 根据岗位信息生成并持久化一套题目。
     *
     * @param request 前端请求
     * @return 新创建的题集详情
     */
    @Transactional
    public QuestionSetDetailResponse generateQuestionSet(GenerateQuestionSetRequest request) {
        PositionProfile profile = resolveProfile(request.positionCode());
        String positionTitle = resolvePositionTitle(request.positionTitle(), profile);
        String difficulty = resolveDifficulty(request.difficulty(), profile);
        List<String> focusAreas = resolveFocusAreas(request.focusAreas(), profile);
        int desiredCount = request.questionCount() == null ? 6 : request.questionCount();
        String locale = StringUtils.hasText(request.locale()) ? request.locale().trim() : "zh-CN";
        String customRequirements = normalizeNullableText(request.customRequirements());

        GenerationCommand command = new GenerationCommand(
            profile != null ? profile.getCode() : null,
            positionTitle,
            difficulty,
            desiredCount,
            customRequirements,
            focusAreas,
            Boolean.TRUE.equals(request.includeScenarioQuestions()),
            locale
        );

        GeneratedInterviewContent content = questionGenerationService.generate(command);
        QuestionSet questionSet = new QuestionSet();
        questionSet.setPositionCode(command.positionCode());
        questionSet.setPositionTitle(positionTitle);
        questionSet.setDifficulty(difficulty);
        questionSet.setCustomRequirements(customRequirements);
        questionSet.setSummary(content.summary());
        questionSet.setSource(content.source());
        questionSet.setStatus(GenerationStatus.COMPLETED);
        questionSet.setAiModel(content.aiModel());
        questionSet.setPromptSnapshot(content.promptSnapshot());
        questionSet.setRawResponse(content.rawResponse());

        List<GeneratedQuestionItem> generatedQuestions = content.questions().stream()
            .limit(desiredCount)
            .toList();
        questionSet.setQuestionCount(generatedQuestions.size());

        for (int index = 0; index < generatedQuestions.size(); index++) {
            GeneratedQuestionItem item = generatedQuestions.get(index);
            InterviewQuestion question = new InterviewQuestion();
            question.setDisplayOrder(index + 1);
            question.setQuestion(item.question());
            question.setAnswerIdea(item.answerIdea());
            question.setFocusPoint(item.focusPoint());
            question.setTags(String.join(",", item.tags()));
            question.setDifficulty(item.difficulty());
            questionSet.addQuestion(question);
        }

        QuestionSet savedQuestionSet = questionSetRepository.save(questionSet);
        return toDetailResponse(savedQuestionSet);
    }

    /**
     * 查询已生成的题集列表。
     *
     * @return 题集摘要列表
     */
    @Transactional(readOnly = true)
    public List<QuestionSetSummaryResponse> listQuestionSets() {
        return questionSetRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(questionSet -> new QuestionSetSummaryResponse(
                questionSet.getId(),
                questionSet.getPositionTitle(),
                questionSet.getDifficulty(),
                questionSet.getQuestionCount(),
                questionSet.getSource().name(),
                questionSet.getAiModel(),
                questionSet.getCreatedAt()
            ))
            .toList();
    }

    /**
     * 根据 ID 查询题集详情。
     *
     * @param id 题集 ID
     * @return 带题目内容的详情
     */
    @Transactional(readOnly = true)
    public QuestionSetDetailResponse getQuestionSet(Long id) {
        QuestionSet questionSet = questionSetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("题集不存在，id=" + id));
        return toDetailResponse(questionSet);
    }

    private PositionProfile resolveProfile(String positionCode) {
        if (!StringUtils.hasText(positionCode)) {
            return null;
        }
        return positionProfileRepository.findByCodeAndActiveTrue(positionCode.trim())
            .orElseThrow(() -> new ResourceNotFoundException("未找到岗位编码: " + positionCode));
    }

    private String resolvePositionTitle(String positionTitle, PositionProfile profile) {
        if (StringUtils.hasText(positionTitle)) {
            return positionTitle.trim();
        }
        if (profile != null) {
            return profile.getTitle();
        }
        throw new IllegalArgumentException("positionCode 或 positionTitle 至少需要传一个。");
    }

    private String resolveDifficulty(String difficulty, PositionProfile profile) {
        if (StringUtils.hasText(difficulty)) {
            return difficulty.trim().toUpperCase();
        }
        return profile != null ? profile.getLevel() : "MID";
    }

    private List<String> resolveFocusAreas(List<String> requestFocusAreas, PositionProfile profile) {
        List<String> focusAreas = requestFocusAreas == null ? new ArrayList<>() : requestFocusAreas.stream()
            .map(this::normalizeNullableText)
            .filter(StringUtils::hasText)
            .toList();

        if (!focusAreas.isEmpty()) {
            return focusAreas;
        }

        if (profile != null && StringUtils.hasText(profile.getDefaultFocusAreas())) {
            return splitTags(profile.getDefaultFocusAreas());
        }

        return List.of("基础知识", "项目实践", "系统设计", "性能优化", "线上排障");
    }

    private String normalizeNullableText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private QuestionSetDetailResponse toDetailResponse(QuestionSet questionSet) {
        List<InterviewQuestionResponse> questions = questionSet.getQuestions().stream()
            .map(question -> new InterviewQuestionResponse(
                question.getId(),
                question.getDisplayOrder(),
                question.getQuestion(),
                question.getAnswerIdea(),
                question.getFocusPoint(),
                splitTags(question.getTags()),
                question.getDifficulty()
            ))
            .toList();

        return new QuestionSetDetailResponse(
            questionSet.getId(),
            questionSet.getPositionCode(),
            questionSet.getPositionTitle(),
            questionSet.getDifficulty(),
            questionSet.getQuestionCount(),
            questionSet.getCustomRequirements(),
            questionSet.getSummary(),
            questionSet.getSource().name(),
            questionSet.getAiModel(),
            questionSet.getCreatedAt(),
            questions
        );
    }

    private List<String> splitTags(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return List.of();
        }
        return Arrays.stream(rawValue.split(","))
            .map(String::trim)
            .filter(value -> !value.isBlank())
            .toList();
    }
}
