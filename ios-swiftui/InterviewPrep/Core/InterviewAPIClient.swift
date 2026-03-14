import Foundation

enum APIClientError: LocalizedError {
    case invalidResponse
    case server(message: String)

    var errorDescription: String? {
        switch self {
        case .invalidResponse:
            return "服务器返回了无法识别的数据。"
        case .server(let message):
            return message
        }
    }
}

/**
 * 统一处理所有后端请求。
 *
 * 这样你后续在 SwiftUI 页面里不需要直接拼 URLRequest，关注视图状态即可。
 */
struct InterviewAPIClient {
    var baseURL: URL = AppEnvironment.baseURL
    var session: URLSession = .shared

    private let encoder: JSONEncoder = {
        let encoder = JSONEncoder()
        encoder.outputFormatting = [.withoutEscapingSlashes]
        return encoder
    }()

    private let decoder = JSONDecoder()

    func fetchPositions() async throws -> [PositionProfile] {
        try await send(path: "/api/positions", method: "GET", body: Optional<Data>.none)
    }

    func fetchQuestionSets() async throws -> [QuestionSetSummary] {
        try await send(path: "/api/question-sets", method: "GET", body: Optional<Data>.none)
    }

    func fetchQuestionSetDetail(id: Int64) async throws -> QuestionSetDetail {
        try await send(path: "/api/question-sets/\(id)", method: "GET", body: Optional<Data>.none)
    }

    func generateQuestionSet(payload: GenerateQuestionSetPayload) async throws -> QuestionSetDetail {
        let requestBody = try encoder.encode(payload)
        return try await send(path: "/api/question-sets/generate", method: "POST", body: requestBody)
    }

    private func send<Response: Decodable>(path: String, method: String, body: Data?) async throws -> Response {
        let normalizedPath = path.trimmingCharacters(in: CharacterSet(charactersIn: "/"))
        var request = URLRequest(url: baseURL.appendingPathComponent(normalizedPath))
        request.httpMethod = method
        request.timeoutInterval = 30
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpBody = body

        let (data, response) = try await session.data(for: request)

        guard let httpResponse = response as? HTTPURLResponse else {
            throw APIClientError.invalidResponse
        }

        guard 200 ..< 300 ~= httpResponse.statusCode else {
            if let backendError = try? decoder.decode(BackendErrorResponse.self, from: data) {
                throw APIClientError.server(message: backendError.message)
            }
            throw APIClientError.server(message: "请求失败，状态码：\(httpResponse.statusCode)")
        }

        do {
            return try decoder.decode(Response.self, from: data)
        } catch {
            throw APIClientError.invalidResponse
        }
    }
}

enum AppEnvironment {
    #if targetEnvironment(simulator)
    private static let simulatorDefaultBaseURL = "http://127.0.0.1:8081"
    #endif

    /**
     * 读取接口基础地址。
     
     * 优先级：
     * 1. 运行时环境变量 `INTERVIEW_API_BASE_URL`
     * 2. 模拟器默认地址 `http://127.0.0.1:8081`
     * 3. 真机读取 Info.plist 里的 `APIBaseURL`
     * 4. 兜底默认值 `http://127.0.0.1:8081`
     */
    static var baseURL: URL {
        if let runtimeValue = ProcessInfo.processInfo.environment["INTERVIEW_API_BASE_URL"],
           let url = URL(string: runtimeValue) {
            return url
        }

        #if targetEnvironment(simulator)
        if let simulatorURL = URL(string: simulatorDefaultBaseURL) {
            return simulatorURL
        }
        #endif

        if let plistValue = Bundle.main.object(forInfoDictionaryKey: "APIBaseURL") as? String,
           let url = URL(string: plistValue) {
            return url
        }

        return URL(string: "http://127.0.0.1:8081")!
    }

    /**
     * 当前实际生效的 API 地址文本。
     */
    static var baseURLText: String {
        baseURL.absoluteString
    }
}
