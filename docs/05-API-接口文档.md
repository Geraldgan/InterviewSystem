# API 接口文档

后端地址默认是：

`http://localhost:8080`

## 1. 健康检查

### `GET /api/health`

响应示例：

```json
{
  "status": "ok"
}
```

## 2. 获取岗位画像

### `GET /api/positions`

响应示例：

```json
[
  {
    "id": 1,
    "code": "ios-mid",
    "title": "iOS中级",
    "category": "移动端",
    "level": "MID",
    "description": "重点考察 Swift / Objective-C、性能优化、架构与实际项目经验。",
    "defaultFocusAreas": ["Swift", "内存管理", "RunLoop"]
  }
]
```

## 3. 生成题集

### `POST /api/question-sets/generate`

请求体示例：

```json
{
  "positionCode": "ios-mid",
  "positionTitle": "iOS中级",
  "customRequirements": "重点考察 Swift 并发和内存管理",
  "questionCount": 6,
  "difficulty": "MID",
  "focusAreas": ["Swift 并发", "内存管理", "组件化"],
  "includeScenarioQuestions": true,
  "locale": "zh-CN"
}
```

响应示例：

```json
{
  "id": 12,
  "positionCode": "ios-mid",
  "positionTitle": "iOS中级",
  "difficulty": "MID",
  "questionCount": 6,
  "customRequirements": "重点考察 Swift 并发和内存管理",
  "summary": "这是一套偏向真实项目场景的 iOS 中级面试题。",
  "source": "OPENAI",
  "aiModel": "gpt-5-mini",
  "createdAt": "2026-03-13T12:30:00",
  "questions": [
    {
      "id": 101,
      "displayOrder": 1,
      "question": "解释一下 Swift 并发中 Task 和 TaskGroup 的使用差异。",
      "answerIdea": "先说模型，再讲项目使用，再补性能与陷阱。",
      "focusPoint": "Swift 并发",
      "tags": ["iOS中级", "Swift 并发", "MID"],
      "difficulty": "MID"
    }
  ]
}
```

## 4. 查询题集列表

### `GET /api/question-sets`

响应示例：

```json
[
  {
    "id": 12,
    "positionTitle": "iOS中级",
    "difficulty": "MID",
    "questionCount": 6,
    "source": "OPENAI",
    "aiModel": "gpt-5-mini",
    "createdAt": "2026-03-13T12:30:00"
  }
]
```

## 5. 查询题集详情

### `GET /api/question-sets/{id}`

返回结构与“生成题集”接口一致。

## 6. 常见错误

统一错误格式：

```json
{
  "timestamp": "2026-03-13T12:31:00",
  "message": "positionCode 或 positionTitle 至少需要传一个。",
  "path": "/api/question-sets/generate"
}
```

## 7. 当前接口实现文件

- [QuestionSetController.java](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/backend/src/main/java/com/interview/backend/controller/QuestionSetController.java)
- [PositionProfileController.java](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/backend/src/main/java/com/interview/backend/controller/PositionProfileController.java)
- [HealthController.java](/Users/geraldgan/Documents/GeraldGan/实践/InterviewSystem/backend/src/main/java/com/interview/backend/controller/HealthController.java)
