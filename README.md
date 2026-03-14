# Interview System

一个面向程序员刷题、整理面试经验、生成个性化题集的项目骨架。

## 你现在拿到的内容

- `backend/`: `Spring Boot` 后端，负责岗位画像、题集生成、OpenAI 调用、本地数据库持久化
- `frontend/`: `uni-app + Vue 3` 客户端，面向 `Web / 微信小程序 / Android`
- `ios-swiftui/`: 原生 `SwiftUI` iOS 客户端骨架
- `docs/`: 从架构到运行、从后端到 iOS 接入的分步文档

## 为什么这样拆

因为你明确说了 iOS 要用 `SwiftUI`，所以我把项目改成了 `API-first` 架构：

- 后端统一提供 REST API
- `Web / 微信小程序 / Android` 共享一套 `uni-app` 前端
- `iOS` 单独走 `SwiftUI`，更符合你的技术栈

## 快速开始

### 1. 启动后端

```bash
cd /Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/backend
export OPENAI_API_KEY="你的 OpenAI Key"
JAVA_TOOL_OPTIONS='-Djavax.net.ssl.trustStoreType=KeychainStore -Djavax.net.ssl.trustStore=NONE' ./gradlew bootRun
```

如果你暂时还没有配置 `OPENAI_API_KEY`，系统也能跑，因为默认会自动回退到本地模拟题生成器。

### 2. 运行 Web 前端

```bash
cd /Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/frontend
npm install
npm run dev:h5
```

如果你想按步骤理解网站端是怎么开发、怎么运行、怎么调试的，直接看：

- [07-网站开发与运行教程.md](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/docs/07-网站开发与运行教程.md)

### 3. 运行微信小程序

```bash
cd /Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/frontend
npm run dev:mp-weixin
```

### 4. 打开 iOS 客户端

查看 [ios-swiftui/README.md](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/ios-swiftui/README.md)。

## 已完成的验证

- 后端 `./gradlew test --no-daemon` 通过
- `SwiftUI` 源码已通过 `swiftc -typecheck`
- `uni-app` 前端已通过 `npm run build:h5`

## 建议你先看哪几份文档

1. [01-架构总览.md](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/docs/01-架构总览.md)
2. [02-后端从零搭建.md](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/docs/02-后端从零搭建.md)
3. [03-多端前端搭建.md](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/docs/03-多端前端搭建.md)
4. [04-iOS-SwiftUI-接入.md](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/docs/04-iOS-SwiftUI-接入.md)
5. [06-微信开发者工具接入.md](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/docs/06-微信开发者工具接入.md)
6. [07-网站开发与运行教程.md](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/docs/07-网站开发与运行教程.md)
