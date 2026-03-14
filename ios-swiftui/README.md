# iOS SwiftUI 客户端

这个目录放的是原生 `SwiftUI` 客户端，现在已经包含可直接打开的：

- `InterviewPrep.xcodeproj`

你可以直接双击它，或者在终端执行：

```bash
open -a Xcode /Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/ios-swiftui/InterviewPrep.xcodeproj
```

## 两种启动方式

### 方式 1：直接打开现成工程

1. 打开 [InterviewPrep.xcodeproj](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/ios-swiftui/InterviewPrep.xcodeproj)
2. 选择 `InterviewPrep` scheme
3. 选择一个 iPhone Simulator
4. 点击 Run

### 方式 2：手动创建 Xcode 工程

1. 打开 Xcode。
2. 新建 `App` 类型工程，语言选 `Swift`，UI 选 `SwiftUI`。
3. 将 `InterviewPrep/` 目录下的文件按目录拖入工程。
4. 保留 `InterviewPrepApp.swift` 作为入口文件。
5. 将 `InterviewAPIClient.swift` 中的 `baseURL` 改成你本机 Spring Boot 的地址。

### 方式 3：使用 XcodeGen 重新生成

如果你安装了 XcodeGen，可以在当前目录执行：

```bash
xcodegen generate
```

然后重新打开生成好的 `InterviewPrep.xcodeproj`。

## 本地联调提醒

- 模拟器默认访问 `http://127.0.0.1:8081`
- 真机当前默认访问 `http://10.0.20.45:8081`
- 如果端口或 IP 变化，也可以在 Xcode Scheme 里给运行环境加 `INTERVIEW_API_BASE_URL=你的地址`
- 当前 `project.yml` 已经放开了本地开发用的 ATS 限制，方便你先联调
