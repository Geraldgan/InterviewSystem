package com.interview.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 核心题库接口测试。
 */
@SpringBootTest
@AutoConfigureMockMvc
class QuestionSetControllerTests {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 验证岗位画像接口能够返回种子数据。
     */
    @Test
    void shouldListSeededPositions() throws Exception {
        mockMvc.perform(get("/api/positions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].code").exists())
            .andExpect(jsonPath("$[0].title").exists());
    }

    /**
     * 验证本地 H5 开发服务的动态端口能够通过跨域校验。
     */
    @Test
    void shouldAllowCorsForLocalH5DevServer() throws Exception {
        mockMvc.perform(options("/api/positions")
                .header(HttpHeaders.ORIGIN, "http://localhost:5174")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5174"));
    }

    /**
     * 验证在未配置 OpenAI Key 时，系统仍可通过本地模拟生成完整题集。
     */
    @Test
    void shouldGenerateQuestionSetUsingMockGenerator() throws Exception {
        String requestBody = """
            {
              "positionCode": "ios-mid",
              "questionCount": 4,
              "difficulty": "MID",
              "customRequirements": "重点考察 Swift 并发与内存管理",
              "includeScenarioQuestions": true
            }
            """;

        mockMvc.perform(post("/api/question-sets/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.source").value("MOCK"))
            .andExpect(jsonPath("$.questionCount").value(4))
            .andExpect(jsonPath("$.questions.length()").value(4));
    }
}
