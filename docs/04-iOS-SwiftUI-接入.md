# iOS SwiftUI 接入

这份文档专门给你看。

## 1. iOS 端现在是什么状态

我已经把 iOS 客户端按 `SwiftUI` 思路拆好了：

- 应用入口
- 网络层
- 数据模型
- 首页
- 历史题集页
- 题集详情页

目录在：

- [ios-swiftui/InterviewPrep](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/ios-swiftui/InterviewPrep)

## 2. 你最先看哪几个文件

1. [InterviewPrepApp.swift](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/ios-swiftui/InterviewPrep/App/InterviewPrepApp.swift)
2. [InterviewStore.swift](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/ios-swiftui/InterviewPrep/Core/InterviewStore.swift)
3. [InterviewAPIClient.swift](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/ios-swiftui/InterviewPrep/Core/InterviewAPIClient.swift)
4. [HomeView.swift](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/ios-swiftui/InterviewPrep/Features/Home/HomeView.swift)
5. [QuestionSetDetailView.swift](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/ios-swiftui/InterviewPrep/Features/Detail/QuestionSetDetailView.swift)

## 3. 怎么理解这个 SwiftUI 架构

如果你习惯 MVVM，可以把它理解成：

- `InterviewAPIClient`: 网络层
- `InterviewStore`: 轻量 ViewModel / Store
- `HomeView / HistoryView / DetailView`: 页面视图
- `InterviewModels.swift`: DTO / Model

## 4. 怎么打开 Xcode 工程

现在已经有可直接打开的工程文件：

- [InterviewPrep.xcodeproj](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/ios-swiftui/InterviewPrep.xcodeproj)

直接打开即可。

## 5. 如果你想重新生成工程

### 方式 A：你自己新建 App 工程

1. 打开 Xcode
2. 新建 `App`
3. Interface 选 `SwiftUI`
4. Language 选 `Swift`
5. 把 `ios-swiftui/InterviewPrep` 里的文件拖进工程

### 方式 B：用 XcodeGen

当前目录已经有：

- [project.yml](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/ios-swiftui/project.yml)

如果你本机装了 `xcodegen`，执行：

```bash
cd /Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/ios-swiftui
xcodegen generate
```

## 6. 接口地址在哪改

文件：

- [InterviewAPIClient.swift](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/ios-swiftui/InterviewPrep/Core/InterviewAPIClient.swift)

现在优先级是：

1. 运行时环境变量 `INTERVIEW_API_BASE_URL`
2. 模拟器默认地址 `http://127.0.0.1:8081`
3. 真机从 `Info.plist` 读取 `APIBaseURL`
4. 兜底默认值 `http://127.0.0.1:8081`

说明：

- iOS 模拟器可直接用 `127.0.0.1`
- 你当前这台电脑在当前 Wi‑Fi 下的局域网 IP 是 `10.0.20.45`
- 所以真机现在应该访问 `http://10.0.20.45:8081`
- 如果以后换了 Wi‑Fi，这个 IP 可能会变，需要同步更新

## 7. 当前页面能力

### 首页

- 读取岗位模板
- 编辑岗位、难度、知识点、自定义要求
- 生成题集

### 历史页

- 查看题集列表
- 跳转到详情

### 详情页

- 查看每道题
- 查看答题思路
- 查看标签

## 8. 已完成验证

我已经执行过 Swift 类型检查：

```bash
xcrun swiftc -typecheck -module-cache-path /tmp/swift-module-cache -sdk "$(xcrun --show-sdk-path --sdk iphonesimulator)" -target arm64-apple-ios18.0-simulator $(find ios-swiftui/InterviewPrep -name '*.swift' | sort)
```

说明当前这些 SwiftUI 源码至少在语法与类型层面是通过的。

除此之外，这个工程还已经完成了：

- `xcodegen` 生成 `.xcodeproj`
- `xcodebuild` 模拟器构建成功
- App 已通过 `simctl` 安装并启动到 iOS Simulator
