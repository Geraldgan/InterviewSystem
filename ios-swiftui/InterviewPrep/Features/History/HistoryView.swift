import SwiftUI

struct HistoryView: View {
    @EnvironmentObject private var store: InterviewStore

    var body: some View {
        List {
            Section {
                ForEach(store.questionSets) { item in
                    NavigationLink {
                        QuestionSetRemoteDetailView(questionSetID: item.id)
                    } label: {
                        VStack(alignment: .leading, spacing: 8) {
                            HStack {
                                Text(item.positionTitle)
                                    .font(.headline)
                                Spacer()
                                Text(item.source)
                                    .font(.caption)
                                    .foregroundStyle(.brown)
                            }
                            Text("\(item.difficulty) · \(item.questionCount) 题 · \(item.aiModel)")
                                .font(.subheadline)
                                .foregroundStyle(.secondary)
                            Text(formatDate(item.createdAt))
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                        .padding(.vertical, 6)
                    }
                }
            } header: {
                Text("历史题集")
            }
        }
        .scrollContentBackground(.hidden)
        .background(
            LinearGradient(
                colors: [Color(red: 0.99, green: 0.96, blue: 0.92), Color(red: 0.95, green: 0.91, blue: 0.86)],
                startPoint: .top,
                endPoint: .bottom
            )
        )
        .navigationTitle("题集")
        .task {
            await store.loadQuestionSets()
        }
        .alert("提示", isPresented: Binding(
            get: { store.errorMessage != nil },
            set: { newValue in
                if !newValue {
                    store.clearError()
                }
            }
        )) {
            Button("知道了", role: .cancel) {
                store.clearError()
            }
        } message: {
            Text(store.errorMessage ?? "")
        }
    }

    private func formatDate(_ rawValue: String) -> String {
        rawValue.replacingOccurrences(of: "T", with: " ").prefix(16).description
    }
}
