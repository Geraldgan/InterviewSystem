package com.interview.backend.controller;

import java.util.List;

import com.interview.backend.dto.GenerateQuestionSetRequest;
import com.interview.backend.dto.QuestionSetDetailResponse;
import com.interview.backend.dto.QuestionSetSummaryResponse;
import com.interview.backend.service.QuestionSetService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 题集接口。
 */
@RestController
@RequestMapping("/api/question-sets")
public class QuestionSetController {

    private final QuestionSetService questionSetService;

    public QuestionSetController(QuestionSetService questionSetService) {
        this.questionSetService = questionSetService;
    }

    /**
     * 生成新的题集。
     *
     * @param request 生成请求
     * @return 新生成的题集详情
     */
    @PostMapping("/generate")
    public QuestionSetDetailResponse generateQuestionSet(@Valid @RequestBody GenerateQuestionSetRequest request) {
        return questionSetService.generateQuestionSet(request);
    }

    /**
     * 获取题集摘要列表。
     *
     * @return 题集列表
     */
    @GetMapping
    public List<QuestionSetSummaryResponse> listQuestionSets() {
        return questionSetService.listQuestionSets();
    }

    /**
     * 根据题集 ID 获取详情。
     *
     * @param id 题集 ID
     * @return 题集详情
     */
    @GetMapping("/{id}")
    public QuestionSetDetailResponse getQuestionSet(@PathVariable Long id) {
        return questionSetService.getQuestionSet(id);
    }
}
