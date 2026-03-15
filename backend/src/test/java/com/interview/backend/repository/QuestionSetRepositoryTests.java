package com.interview.backend.repository;

import com.interview.backend.entity.GenerationSource;
import com.interview.backend.entity.GenerationStatus;
import com.interview.backend.entity.InterviewQuestion;
import com.interview.backend.entity.QuestionSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 验证长文本字段在数据库中可以正常持久化，避免 MySQL 文本列过小导致截断。
 */
@DataJpaTest
class QuestionSetRepositoryTests {

    @Autowired
    private QuestionSetRepository questionSetRepository;

    @Test
    void shouldPersistLongPromptSnapshotAndQuestionContent() {
        QuestionSet questionSet = new QuestionSet();
        questionSet.setPositionCode("ios-mid");
        questionSet.setPositionTitle("iOS 中级工程师");
        questionSet.setDifficulty("MID");
        questionSet.setQuestionCount(1);
        questionSet.setCustomRequirements("重点考察长文本落库能力");
        questionSet.setSummary("长文本持久化回归测试");
        questionSet.setSource(GenerationSource.MOCK);
        questionSet.setStatus(GenerationStatus.COMPLETED);
        questionSet.setAiModel("gpt-5-mini");
        questionSet.setPromptSnapshot("prompt-".repeat(400));
        questionSet.setRawResponse("response-".repeat(800));

        InterviewQuestion question = new InterviewQuestion();
        question.setDisplayOrder(1);
        question.setQuestion("question-".repeat(400));
        question.setAnswerIdea("answer-".repeat(400));
        question.setFocusPoint("持久化");
        question.setTags("数据库,MySQL");
        question.setDifficulty("MID");
        questionSet.addQuestion(question);

        QuestionSet saved = questionSetRepository.saveAndFlush(questionSet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPromptSnapshot()).hasSizeGreaterThan(255);
        assertThat(saved.getQuestions()).hasSize(1);
        assertThat(saved.getQuestions().get(0).getQuestion()).hasSizeGreaterThan(255);
        assertThat(saved.getQuestions().get(0).getAnswerIdea()).hasSizeGreaterThan(255);
    }
}
