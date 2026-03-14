package com.interview.backend.service;

import java.util.List;

import com.interview.backend.entity.PositionProfile;
import com.interview.backend.repository.PositionProfileRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 启动时自动插入演示岗位，方便你第一次运行就有内容可看。
 */
@Component
public class SeedDataInitializer implements ApplicationRunner {

    private final PositionProfileRepository positionProfileRepository;

    public SeedDataInitializer(PositionProfileRepository positionProfileRepository) {
        this.positionProfileRepository = positionProfileRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (positionProfileRepository.count() > 0) {
            return;
        }

        positionProfileRepository.saveAll(List.of(
            createProfile(
                "ios-mid",
                "iOS中级",
                "移动端",
                "MID",
                "重点考察 Swift / Objective-C、性能优化、架构与实际项目经验。",
                "Swift,内存管理,RunLoop,多线程,网络层设计,性能优化"
            ),
            createProfile(
                "java-backend",
                "Java后端",
                "服务端",
                "MID",
                "重点考察 Java 基础、Spring 生态、数据库与高并发场景。",
                "Java集合,并发编程,Spring Boot,MySQL,Redis,系统设计"
            ),
            createProfile(
                "ai-agent-engineer",
                "AI Agent工程师",
                "AI应用",
                "SENIOR",
                "重点考察 LLM 应用架构、Prompt 设计、工具调用与可观测性。",
                "Prompt设计,RAG,工具调用,工作流编排,评测体系,安全与成本优化"
            ),
            createProfile(
                "frontend-mid",
                "前端中级",
                "Web",
                "MID",
                "重点考察浏览器原理、工程化、性能优化与前后端协作。",
                "JavaScript,TypeScript,Vue/React,构建工程化,性能优化,跨端适配"
            )
        ));
    }

    private PositionProfile createProfile(
        String code,
        String title,
        String category,
        String level,
        String description,
        String focusAreas
    ) {
        PositionProfile profile = new PositionProfile();
        profile.setCode(code);
        profile.setTitle(title);
        profile.setCategory(category);
        profile.setLevel(level);
        profile.setDescription(description);
        profile.setDefaultFocusAreas(focusAreas);
        profile.setActive(true);
        return profile;
    }
}
