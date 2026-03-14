import SwiftUI

struct QuestionSetDetailView: View {
    let detail: QuestionSetDetail

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 18) {
                VStack(alignment: .leading, spacing: 10) {
                    Text(detail.positionTitle)
                        .font(.system(size: 30, weight: .heavy, design: .rounded))
                    Text("\(detail.difficulty) · \(detail.questionCount) 题 · \(detail.aiModel)")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                    Text(detail.summary)
                        .font(.body)
                        .foregroundStyle(.secondary)
                    if let customRequirements = detail.customRequirements, !customRequirements.isEmpty {
                        Text("自定义要求：\(customRequirements)")
                            .font(.footnote)
                            .foregroundStyle(.brown)
                    }
                }
                .padding(24)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(.ultraThinMaterial)
                .clipShape(RoundedRectangle(cornerRadius: 28, style: .continuous))

                ForEach(detail.questions) { question in
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Q\(question.displayOrder)")
                            .font(.caption.weight(.bold))
                            .foregroundStyle(.white)
                            .padding(.horizontal, 12)
                            .padding(.vertical, 8)
                            .background(
                                LinearGradient(colors: [.brown, .orange], startPoint: .topLeading, endPoint: .bottomTrailing)
                            )
                            .clipShape(Capsule())

                        Text(question.question)
                            .font(.headline)

                        FlowLayout(items: question.tags) { tag in
                            TagChip(title: tag)
                        }

                        Text("答题思路")
                            .font(.subheadline.weight(.semibold))
                            .foregroundStyle(.brown)
                        Text(question.answerIdea)
                            .font(.body)
                            .foregroundStyle(.secondary)
                    }
                    .padding(22)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color.white.opacity(0.88))
                    .clipShape(RoundedRectangle(cornerRadius: 24, style: .continuous))
                }
            }
            .padding(20)
        }
        .background(
            LinearGradient(
                colors: [Color(red: 0.99, green: 0.96, blue: 0.92), Color(red: 0.95, green: 0.91, blue: 0.86)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            .ignoresSafeArea()
        )
        .navigationTitle("题集详情")
        .navigationBarTitleDisplayMode(.inline)
    }
}
