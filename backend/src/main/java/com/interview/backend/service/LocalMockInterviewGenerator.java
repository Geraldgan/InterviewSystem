package com.interview.backend.service;

import java.util.ArrayList;
import java.util.List;

import com.interview.backend.entity.GenerationSource;
import com.interview.backend.support.GenerationCommand;
import com.interview.backend.support.GeneratedInterviewContent;
import com.interview.backend.support.GeneratedQuestionItem;
import com.interview.backend.support.InterviewPromptBuilder;
import org.springframework.stereotype.Component;

/**
 * OpenAI Key 不可用时的本地模拟题生成器。
 */
@Component
public class LocalMockInterviewGenerator {

    private static final List<String> QUESTION_TEMPLATES = List.of(
        "请结合你做过的项目，说明在%s方面遇到过什么问题，以及你如何定位和解决。",
        "如果让你面试一位%s候选人，你会怎样判断他是否真正掌握%s？",
        "请设计一个围绕%s的核心模块，并说明关键技术取舍。",
        "当业务规模快速增长时，%s相关能力通常会遇到什么瓶颈？你会如何优化？",
        "请回答一道与%s相关的高频基础题，并说明面试官期望听到的回答结构。"
    );

    private final InterviewPromptBuilder promptBuilder;

    public LocalMockInterviewGenerator(InterviewPromptBuilder promptBuilder) {
        this.promptBuilder = promptBuilder;
    }

    /**
     * 生成一套可演示的本地题目。
     *
     * @param command 生成命令
     * @return 统一格式的题集结果
     */
    public GeneratedInterviewContent generate(GenerationCommand command) {
        List<GeneratedQuestionItem> questions = new ArrayList<>();
        List<String> focusAreas = command.focusAreas().isEmpty()
            ? List.of("基础知识", "项目实践", "系统设计", "性能优化", "问题排查")
            : command.focusAreas();

        for (int index = 0; index < command.questionCount(); index++) {
            String focusArea = focusAreas.get(index % focusAreas.size());
            String template = QUESTION_TEMPLATES.get(index % QUESTION_TEMPLATES.size());
            String question = template.formatted(command.positionTitle(), focusArea);
            String answerIdea = """
                1. 先给出 %s 的核心概念、目标和适用场景。
                2. 再结合真实项目描述你的方案、关键取舍和落地结果。
                3. 最后补充一个常见风险点，以及你如何验证和优化。
                """.formatted(focusArea).trim();

            questions.add(new GeneratedQuestionItem(
                question,
                answerIdea,
                focusArea,
                List.of(command.positionTitle(), focusArea, command.difficulty()),
                command.difficulty()
            ));
        }

        String summary = "%s 岗位的演示题集，适合先联调前后端，再切换为真实 OpenAI 生成。".formatted(command.positionTitle());
        return new GeneratedInterviewContent(
            summary,
            questions,
            GenerationSource.MOCK,
            "local-mock-generator",
            promptBuilder.buildPrompt(command),
            "{\"source\":\"mock\"}"
        );
    }
}
