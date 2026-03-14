import Foundation

@MainActor
final class InterviewStore: ObservableObject {
    @Published private(set) var positions: [PositionProfile] = []
    @Published private(set) var questionSets: [QuestionSetSummary] = []
    @Published var errorMessage: String?

    private let apiClient: InterviewAPIClient

    init(apiClient: InterviewAPIClient = InterviewAPIClient()) {
        self.apiClient = apiClient
    }

    /**
     * 加载岗位列表，首页首次进入时调用。
     */
    func loadPositions() async {
        do {
            positions = try await apiClient.fetchPositions()
        } catch {
            capture(error)
        }
    }

    /**
     * 加载历史题集。
     */
    func loadQuestionSets() async {
        do {
            questionSets = try await apiClient.fetchQuestionSets()
        } catch {
            capture(error)
        }
    }

    /**
     * 提交生成请求。
     *
     * @param form 用户在页面上填写的表单
     * @return 新生成的题集详情
     */
    func generateQuestionSet(form: GenerateQuestionForm) async -> QuestionSetDetail? {
        do {
            let detail = try await apiClient.generateQuestionSet(payload: form.payload)
            return detail
        } catch {
            capture(error)
            return nil
        }
    }

    /**
     * 获取单个题集详情。
     */
    func fetchQuestionSetDetail(id: Int64) async -> QuestionSetDetail? {
        do {
            return try await apiClient.fetchQuestionSetDetail(id: id)
        } catch {
            capture(error)
            return nil
        }
    }

    func clearError() {
        errorMessage = nil
    }

    private func capture(_ error: Error) {
        errorMessage = error.localizedDescription
    }
}
