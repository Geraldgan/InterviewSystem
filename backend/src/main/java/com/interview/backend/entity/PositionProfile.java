package com.interview.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * 岗位画像实体，用于预设不同岗位的知识点和描述。
 */
@Entity
@Table(name = "position_profiles")
public class PositionProfile extends BaseEntity {

    @Column(nullable = false, unique = true, length = 80)
    private String code;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 60)
    private String category;

    @Column(nullable = false, length = 40)
    private String level;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, length = 1000)
    private String defaultFocusAreas;

    @Column(nullable = false)
    private boolean active = true;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultFocusAreas() {
        return defaultFocusAreas;
    }

    public void setDefaultFocusAreas(String defaultFocusAreas) {
        this.defaultFocusAreas = defaultFocusAreas;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
