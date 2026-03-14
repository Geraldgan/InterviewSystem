import SwiftUI

struct HomeView: View {
    @EnvironmentObject private var store: InterviewStore

    @State private var form = GenerateQuestionForm()
    @State private var isGenerating = false
    @State private var generatedQuestionSetID: Int64?

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                headerCard
                positionSection
                formSection
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
        .navigationTitle("智能出题")
        .task {
            if store.positions.isEmpty {
                await store.loadPositions()
            }
            if let first = store.positions.first, form.positionTitle.isEmpty {
                apply(profile: first)
            }
        }
        .navigationDestination(isPresented: Binding(
            get: { generatedQuestionSetID != nil },
            set: { newValue in
                if !newValue {
                    generatedQuestionSetID = nil
                }
            }
        )) {
            if let id = generatedQuestionSetID {
                QuestionSetRemoteDetailView(questionSetID: id)
            }
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

    private var headerCard: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Interview Craft")
                .font(.caption.weight(.semibold))
                .tracking(3)
                .foregroundStyle(.brown)
            Text("为目标岗位生成专属面试题")
                .font(.system(size: 30, weight: .heavy, design: .rounded))
            Text("你在 iOS 端只需要关心 SwiftUI 体验，题目生成、岗位画像和数据存储都由 Spring Boot 服务端统一承载。")
                .font(.subheadline)
                .foregroundStyle(.secondary)

            VStack(alignment: .leading, spacing: 6) {
                Text("当前 API 地址")
                    .font(.caption.weight(.semibold))
                    .foregroundStyle(.brown)
                Text(AppEnvironment.baseURLText)
                    .font(.caption.monospaced())
                    .foregroundStyle(.primary)
                    .textSelection(.enabled)
            }
            .padding(.top, 4)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(24)
        .background(.ultraThinMaterial)
        .clipShape(RoundedRectangle(cornerRadius: 28, style: .continuous))
    }

    private var positionSection: some View {
        VStack(alignment: .leading, spacing: 14) {
            Text("岗位模板")
                .font(.headline)

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 14) {
                    ForEach(store.positions) { profile in
                        Button {
                            apply(profile: profile)
                        } label: {
                            VStack(alignment: .leading, spacing: 8) {
                                Text(profile.title)
                                    .font(.headline)
                                    .foregroundStyle(.primary)
                                Text("\(profile.category) · \(profile.level)")
                                    .font(.caption)
                                    .foregroundStyle(.brown)
                                Text(profile.description)
                                    .font(.footnote)
                                    .foregroundStyle(.secondary)
                                    .multilineTextAlignment(.leading)
                            }
                            .padding(18)
                            .frame(width: 260, alignment: .leading)
                            .background(form.positionCode == profile.code ? Color.orange.opacity(0.18) : Color.white.opacity(0.8))
                            .clipShape(RoundedRectangle(cornerRadius: 24, style: .continuous))
                        }
                    }
                }
            }
        }
    }

    private var formSection: some View {
        VStack(alignment: .leading, spacing: 18) {
            Text("出题参数")
                .font(.headline)

            Group {
                TextField("岗位名称，例如 AI Agent 工程师", text: $form.positionTitle)
                TextField("难度，例如 MID / SENIOR", text: $form.difficulty)
                Stepper("题目数量：\(form.questionCount)", value: $form.questionCount, in: 3 ... 20)
                TextField("关注知识点，使用逗号或换行分隔", text: $form.focusAreasText, axis: .vertical)
                    .lineLimit(4, reservesSpace: true)
                TextField("补充要求，例如重点考察 Swift 并发", text: $form.customRequirements, axis: .vertical)
                    .lineLimit(4, reservesSpace: true)
                Toggle("至少包含场景题", isOn: $form.includeScenarioQuestions)
            }
            .textFieldStyle(.roundedBorder)

            if let currentProfile = store.positions.first(where: { $0.code == form.positionCode }) {
                VStack(alignment: .leading, spacing: 8) {
                    Text("默认考点")
                        .font(.subheadline.weight(.semibold))
                    WrapView(items: currentProfile.defaultFocusAreas)
                }
            }

            Button {
                Task {
                    isGenerating = true
                    defer { isGenerating = false }

                    if let detail = await store.generateQuestionSet(form: form) {
                        generatedQuestionSetID = detail.id
                    }
                }
            } label: {
                HStack {
                    Spacer()
                    if isGenerating {
                        ProgressView()
                            .tint(.white)
                    }
                    Text(isGenerating ? "正在生成..." : "生成题集")
                        .font(.headline)
                    Spacer()
                }
                .padding(.vertical, 14)
                .background(
                    LinearGradient(colors: [.brown, .orange], startPoint: .leading, endPoint: .trailing)
                )
                .foregroundStyle(.white)
                .clipShape(RoundedRectangle(cornerRadius: 20, style: .continuous))
            }
            .buttonStyle(.plain)
        }
        .padding(24)
        .background(.thinMaterial)
        .clipShape(RoundedRectangle(cornerRadius: 28, style: .continuous))
    }

    private func apply(profile: PositionProfile) {
        form.positionCode = profile.code
        form.positionTitle = profile.title
        form.difficulty = profile.level
        form.focusAreasText = profile.defaultFocusAreas.joined(separator: "、")
    }
}

private struct WrapView: View {
    let items: [String]

    var body: some View {
        FlowLayout(items: items) { item in
            TagChip(title: item)
        }
    }
}
