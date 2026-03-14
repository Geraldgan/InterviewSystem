import Foundation

struct PositionProfile: Identifiable, Codable, Hashable {
    let id: Int64
    let code: String
    let title: String
    let category: String
    let level: String
    let description: String
    let defaultFocusAreas: [String]
}

struct InterviewQuestion: Identifiable, Codable, Hashable {
    let id: Int64
    let displayOrder: Int
    let question: String
    let answerIdea: String
    let focusPoint: String
    let tags: [String]
    let difficulty: String
}

struct QuestionSetSummary: Identifiable, Codable, Hashable {
    let id: Int64
    let positionTitle: String
    let difficulty: String
    let questionCount: Int
    let source: String
    let aiModel: String
    let createdAt: String
}

struct QuestionSetDetail: Identifiable, Codable, Hashable {
    let id: Int64
    let positionCode: String?
    let positionTitle: String
    let difficulty: String
    let questionCount: Int
    let customRequirements: String?
    let summary: String
    let source: String
    let aiModel: String
    let createdAt: String
    let questions: [InterviewQuestion]
}

struct GenerateQuestionSetPayload: Encodable {
    let positionCode: String?
    let positionTitle: String
    let customRequirements: String?
    let questionCount: Int
    let difficulty: String
    let focusAreas: [String]
    let includeScenarioQuestions: Bool
    let locale: String
}

struct BackendErrorResponse: Decodable {
    let timestamp: String
    let message: String
    let path: String
}

struct GenerateQuestionForm {
    var positionCode: String?
    var positionTitle: String = ""
    var customRequirements: String = ""
    var questionCount: Int = 6
    var difficulty: String = "MID"
    var focusAreasText: String = ""
    var includeScenarioQuestions: Bool = true
    var locale: String = "zh-CN"

    /**
     * 把文本框里的知识点拆成数组，和后端 DTO 保持一致。
     */
    var focusAreas: [String] {
        focusAreasText
            .split(whereSeparator: { [",", "，", "、", "\n"].contains($0) })
            .map { $0.trimmingCharacters(in: .whitespacesAndNewlines) }
            .filter { !$0.isEmpty }
    }

    var payload: GenerateQuestionSetPayload {
        GenerateQuestionSetPayload(
            positionCode: positionCode,
            positionTitle: positionTitle,
            customRequirements: customRequirements.isEmpty ? nil : customRequirements,
            questionCount: questionCount,
            difficulty: difficulty,
            focusAreas: focusAreas,
            includeScenarioQuestions: includeScenarioQuestions,
            locale: locale
        )
    }
}
