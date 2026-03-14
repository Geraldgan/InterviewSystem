package com.interview.backend.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

/**
 * 一次题目生成请求对应的题集。
 */
@Entity
@Table(name = "question_sets")
public class QuestionSet extends BaseEntity {

    @Column(length = 80)
    private String positionCode;

    @Column(nullable = false, length = 120)
    private String positionTitle;

    @Column(nullable = false, length = 40)
    private String difficulty;

    @Column(nullable = false)
    private Integer questionCount;

    @Column(length = 600)
    private String customRequirements;

    @Column(length = 500)
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GenerationSource source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GenerationStatus status;

    @Column(nullable = false, length = 80)
    private String aiModel;

    @Lob
    @Column(nullable = false)
    private String promptSnapshot;

    @Lob
    private String rawResponse;

    @OrderBy("displayOrder ASC")
    @OneToMany(mappedBy = "questionSet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InterviewQuestion> questions = new ArrayList<>();

    public void addQuestion(InterviewQuestion question) {
        question.setQuestionSet(this);
        this.questions.add(question);
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public String getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public String getCustomRequirements() {
        return customRequirements;
    }

    public void setCustomRequirements(String customRequirements) {
        this.customRequirements = customRequirements;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public GenerationSource getSource() {
        return source;
    }

    public void setSource(GenerationSource source) {
        this.source = source;
    }

    public GenerationStatus getStatus() {
        return status;
    }

    public void setStatus(GenerationStatus status) {
        this.status = status;
    }

    public String getAiModel() {
        return aiModel;
    }

    public void setAiModel(String aiModel) {
        this.aiModel = aiModel;
    }

    public String getPromptSnapshot() {
        return promptSnapshot;
    }

    public void setPromptSnapshot(String promptSnapshot) {
        this.promptSnapshot = promptSnapshot;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    public List<InterviewQuestion> getQuestions() {
        return questions;
    }
}
