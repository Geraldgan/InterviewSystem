import SwiftUI

struct QuestionSetRemoteDetailView: View {
    @EnvironmentObject private var store: InterviewStore
    let questionSetID: Int64

    @State private var detail: QuestionSetDetail?
    @State private var isLoading = false

    var body: some View {
        Group {
            if let detail {
                QuestionSetDetailView(detail: detail)
            } else if isLoading {
                ProgressView("正在加载题集详情...")
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(Color(.systemGroupedBackground))
            } else {
                ContentUnavailableView("暂无题集详情", systemImage: "doc.text.magnifyingglass")
            }
        }
        .task {
            guard detail == nil else { return }
            isLoading = true
            detail = await store.fetchQuestionSetDetail(id: questionSetID)
            isLoading = false
        }
    }
}
