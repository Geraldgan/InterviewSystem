package com.interview.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * 题集中的单道题。
 */
@Entity
@Table(name = "interview_questions")
public class InterviewQuestion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_set_id", nullable = false)
    private QuestionSet questionSet;

    @Column(nullable = false)
    private Integer displayOrder;

    @Lob
    @Column(nullable = false)
    private String question;

    @Lob
    @Column(nullable = false)
    private String answerIdea;

    @Column(nullable = false, length = 200)
    private String focusPoint;

    @Column(nullable = false, length = 500)
    private String tags;

    @Column(nullable = false, length = 40)
    private String difficulty;

    public QuestionSet getQuestionSet() {
        return questionSet;
    }

    public void setQuestionSet(QuestionSet questionSet) {
        this.questionSet = questionSet;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswerIdea() {
        return answerIdea;
    }

    public void setAnswerIdea(String answerIdea) {
        this.answerIdea = answerIdea;
    }

    public String getFocusPoint() {
        return focusPoint;
    }

    public void setFocusPoint(String focusPoint) {
        this.focusPoint = focusPoint;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
