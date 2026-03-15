# QuestionSetService 详解

这份文档专门解释：

- `QuestionSetService` 这个类到底负责什么
- `generateQuestionSet()` 为什么要写这么多辅助方法
- 它是怎么同时调用“岗位表”和“题集表”的
- 它是怎么把 AI / Mock 生成结果保存进数据库的
- `listQuestionSets()` 和 `getQuestionSet()` 又分别在做什么

如果你是从 `iOS + SwiftUI` 视角来看，可以先把它理解成：

- `Controller` 负责接接口请求
- `Service` 负责核心业务逻辑
- `Repository` 负责数据库访问
- `Entity` 负责和数据库表映射
- `DTO / Response` 负责给前端返回结果
- `QuestionGenerationService` 像一个“生成策略协调器”

---

## 1. 这个类在整个项目里的位置

`QuestionSetService` 是整个项目里最核心的业务类之一。

它负责三件事：

1. 根据前端请求生成一套题目
2. 把题集和题目持久化到数据库
3. 查询题集列表和题集详情

它对应的接口在：

- `POST /api/question-sets/generate`
- `GET /api/question-sets`
- `GET /api/question-sets/{id}`

也就是说：

- “生成题集”
- “查看历史题集列表”
- “查看某一套题的详情”

这三条功能，最后都会走到 `QuestionSetService`。

---

## 2. 先看整体调用链

### 生成题集时

```text
前端
  -> QuestionSetController
  -> QuestionSetService.generateQuestionSet()
  -> PositionProfileRepository 查岗位画像
  -> QuestionGenerationService 生成题目
  -> QuestionSetRepository 保存题集和题目
  -> QuestionSetService 转成详情响应
  -> 返回给前端
```

### 查看题集列表时

```text
前端
  -> QuestionSetController
  -> QuestionSetService.listQuestionSets()
  -> QuestionSetRepository 查题集表
  -> 转成摘要 DTO
  -> 返回给前端
```

### 查看题集详情时

```text
前端
  -> QuestionSetController
  -> QuestionSetService.getQuestionSet(id)
  -> QuestionSetRepository 查题集 + 题目
  -> 转成详情 DTO
  -> 返回给前端
```

---

## 3. Controller 做了什么

文件：

- `backend/src/main/java/com/interview/backend/controller/QuestionSetController.java`

核心代码可以概括成这样：

```java
@PostMapping("/generate")
public QuestionSetDetailResponse generateQuestionSet(@Valid @RequestBody GenerateQuestionSetRequest request) {
    return questionSetService.generateQuestionSet(request);
}

@GetMapping
public List<QuestionSetSummaryResponse> listQuestionSets() {
    return questionSetService.listQuestionSets();
}

@GetMapping("/{id}")
public QuestionSetDetailResponse getQuestionSet(@PathVariable Long id) {
    return questionSetService.getQuestionSet(id);
}
```

这里和 `PositionProfileController` 一样，Controller 本身几乎不做业务。

它只是：

- 接收 HTTP 请求
- 做参数绑定
- 把请求转给 Service

真正复杂的逻辑都在 `QuestionSetService`。

---

## 4. `QuestionSetService` 依赖了哪些东西

`QuestionSetService` 里有 3 个核心依赖：

```java
private final PositionProfileRepository positionProfileRepository;
private final QuestionSetRepository questionSetRepository;
private final QuestionGenerationService questionGenerationService;
```

可以这样理解：

- `PositionProfileRepository`
  负责查岗位画像表

- `QuestionSetRepository`
  负责查和存题集表

- `QuestionGenerationService`
  负责真正“生成题”

所以 `QuestionSetService` 的定位是：

```text
把岗位信息、前端参数、生成逻辑、数据库存储 串在一起
```

---

## 5. 先看输入：前端会传什么

前端请求对象是：

- `backend/src/main/java/com/interview/backend/dto/GenerateQuestionSetRequest.java`

结构如下：

```java
public record GenerateQuestionSetRequest(
    String positionCode,
    String positionTitle,
    String customRequirements,
    Integer questionCount,
    String difficulty,
    List<String> focusAreas,
    Boolean includeScenarioQuestions,
    String locale
) {
}
```

这几个字段的意思：

- `positionCode`
  预设岗位编码，比如 `ios-mid`

- `positionTitle`
  自定义岗位名，比如 `AI Agent工程师`

- `customRequirements`
  自定义要求，比如“重点考察 Swift 并发”

- `questionCount`
  想生成几道题

- `difficulty`
  难度，比如 `MID`

- `focusAreas`
  关注知识点列表

- `includeScenarioQuestions`
  是否包含场景题

- `locale`
  输出语言

这里有一个很重要的设计：

- 前端既可以只传 `positionCode`
- 也可以只传 `positionTitle`
- 甚至可以传两者，但至少要有一个能确定岗位信息

---

## 6. `generateQuestionSet()` 整体在做什么

这是最核心的方法。

你可以把它分成 6 步：

1. 先补齐和标准化请求参数
2. 组装成一个统一的 `GenerationCommand`
3. 调用生成服务拿到题集结果
4. 把结果映射成 `QuestionSet` 和 `InterviewQuestion` 实体
5. 保存到数据库
6. 转成前端要的详情响应

换成伪代码就是：

```text
接请求
-> 补默认值
-> 查岗位画像
-> 组装生成命令
-> 生成题目
-> 转数据库实体
-> 保存
-> 转响应 DTO
-> 返回
```

---

## 7. 第一步：先补齐请求参数

`generateQuestionSet()` 一开始做了这些事：

```java
PositionProfile profile = resolveProfile(request.positionCode());
String positionTitle = resolvePositionTitle(request.positionTitle(), profile);
String difficulty = resolveDifficulty(request.difficulty(), profile);
List<String> focusAreas = resolveFocusAreas(request.focusAreas(), profile);
int desiredCount = request.questionCount() == null ? 6 : request.questionCount();
String locale = StringUtils.hasText(request.locale()) ? request.locale().trim() : "zh-CN";
String customRequirements = normalizeNullableText(request.customRequirements());
```

这段逻辑的核心思想是：

- 前端传什么不一定完整
- Service 要把“不完整请求”补成“完整可执行命令”

### `resolveProfile(positionCode)`

如果前端传了 `positionCode`，就去岗位画像表查这一条岗位：

```java
return positionProfileRepository.findByCodeAndActiveTrue(positionCode.trim())
    .orElseThrow(() -> new ResourceNotFoundException("未找到岗位编码: " + positionCode));
```

也就是说：

- `ios-mid` 这种预设岗位，会去数据库查
- 如果查不到，直接抛异常

这一步访问的是 `position_profiles` 表，不是 `question_sets` 表。

### `resolvePositionTitle(positionTitle, profile)`

逻辑顺序是：

1. 如果前端显式传了 `positionTitle`，优先用前端传的
2. 否则如果查到了 `profile`，就用岗位画像里的 `title`
3. 如果两个都没有，直接报错

所以这个方法是在保证：

- 生成题目时一定能得到一个岗位名称

### `resolveDifficulty(difficulty, profile)`

逻辑顺序：

1. 前端传了就用前端传的
2. 没传就用岗位画像里的 `level`
3. 再不行就默认 `MID`

### `resolveFocusAreas(focusAreas, profile)`

逻辑顺序：

1. 前端传了 `focusAreas` 就优先用
2. 否则如果岗位画像里有默认知识点，就用岗位画像里的
3. 否则回退到固定默认值：

```java
List.of("基础知识", "项目实践", "系统设计", "性能优化", "线上排障");
```

### `desiredCount`

如果前端没传题目数量，默认生成 6 道。

### `locale`

如果前端没传语言，默认是 `zh-CN`。

### `normalizeNullableText`

这个方法很简单：

- 有值就 `trim()`
- 没值就返回 `null`

它的作用是避免把全空格字符串保存进数据库。

---

## 8. 第二步：为什么还要组一个 `GenerationCommand`

Service 接下来没有直接把 `request` 传给生成器，而是先组了一个：

```java
GenerationCommand command = new GenerationCommand(
    profile != null ? profile.getCode() : null,
    positionTitle,
    difficulty,
    desiredCount,
    customRequirements,
    focusAreas,
    Boolean.TRUE.equals(request.includeScenarioQuestions()),
    locale
);
```

这个 `GenerationCommand` 的意义是：

- 把前端原始请求
- 加上 Service 已经补好的默认值
- 变成一个“生成器能稳定使用的统一输入对象”

也就是说，生成器不需要关心：

- 前端到底传没传某个字段
- 默认值该怎么补
- 岗位编码能不能查到

这些都由 `QuestionSetService` 提前处理好了。

所以 `GenerationCommand` 更像一个：

```text
已经标准化的内部命令对象
```

---

## 9. 第三步：真正生成题目的是谁

Service 接着调用：

```java
GeneratedInterviewContent content = questionGenerationService.generate(command);
```

注意：

- 真正“出题”的不是 `QuestionSetService`
- 真正“出题”的是 `QuestionGenerationService`

文件：

- `backend/src/main/java/com/interview/backend/service/QuestionGenerationService.java`

这个类会决定：

- 有 `OPENAI_API_KEY` 就走 OpenAI
- 没有的话就走本地 Mock 生成器

它返回的是统一结构：

- `GeneratedInterviewContent`

这个对象里包含：

- `summary`
- `questions`
- `source`
- `aiModel`
- `promptSnapshot`
- `rawResponse`

也就是说，`QuestionSetService` 并不关心题是 OpenAI 生成的还是 Mock 生成的。

它只关心：

- 我拿到的生成结果结构是统一的
- 我只要把它存起来并返回就行

这就是一个很典型的“策略隔离”设计。

---

## 10. 第四步：生成结果怎么变成数据库实体

生成完成后，`QuestionSetService` 开始组装数据库实体。

### 先组装题集主表 `QuestionSet`

代码大概是：

```java
QuestionSet questionSet = new QuestionSet();
questionSet.setPositionCode(command.positionCode());
questionSet.setPositionTitle(positionTitle);
questionSet.setDifficulty(difficulty);
questionSet.setCustomRequirements(customRequirements);
questionSet.setSummary(content.summary());
questionSet.setSource(content.source());
questionSet.setStatus(GenerationStatus.COMPLETED);
questionSet.setAiModel(content.aiModel());
questionSet.setPromptSnapshot(content.promptSnapshot());
questionSet.setRawResponse(content.rawResponse());
```

这里你可以理解成：

- 把“生成结果的元信息”放进 `question_sets` 主表

例如：

- 岗位名
- 难度
- 总结摘要
- 来源是 `MOCK` 还是 `OPENAI`
- 用了哪个模型
- 发给模型的 Prompt
- 模型原始返回

### 再从生成结果里取题目列表

```java
List<GeneratedQuestionItem> generatedQuestions = content.questions().stream()
    .limit(desiredCount)
    .toList();
questionSet.setQuestionCount(generatedQuestions.size());
```

这里有一个细节很重要：

- 即使生成器给了更多题
- Service 也会用 `.limit(desiredCount)` 截到你真正想要的数量

所以最终落库的题目数，始终以 `desiredCount` 为上限。

---

## 11. 第五步：题目子表是怎么挂到题集上的

接下来是一个 `for` 循环：

```java
for (int index = 0; index < generatedQuestions.size(); index++) {
    GeneratedQuestionItem item = generatedQuestions.get(index);
    InterviewQuestion question = new InterviewQuestion();
    question.setDisplayOrder(index + 1);
    question.setQuestion(item.question());
    question.setAnswerIdea(item.answerIdea());
    question.setFocusPoint(item.focusPoint());
    question.setTags(String.join(",", item.tags()));
    question.setDifficulty(item.difficulty());
    questionSet.addQuestion(question);
}
```

这段逻辑做的是：

- 每一个 `GeneratedQuestionItem`
- 转成一个 `InterviewQuestion` 实体

这里有两个点很重要。

### 1. `displayOrder = index + 1`

这是为了给每道题一个稳定顺序。

因为前端展示时通常要按：

- 第 1 题
- 第 2 题
- 第 3 题

这样的顺序来显示。

### 2. `tags` 在数据库里存成字符串

生成结果里 `tags` 是一个列表：

```java
List<String>
```

但是数据库字段是一个字符串，所以这里先做：

```java
String.join(",", item.tags())
```

例如：

```text
["iOS中级", "Swift并发", "MID"]
```

会被存成：

```text
iOS中级,Swift并发,MID
```

等到返回给前端时，再通过 `splitTags()` 拆回列表。

### 3. `questionSet.addQuestion(question)` 为什么不是直接 `getQuestions().add(question)`

因为 `addQuestion()` 里除了 `add`，还做了一件事：

```java
question.setQuestionSet(this);
this.questions.add(question);
```

也就是说它会同时维护双向关系：

- 子题目知道自己属于哪个 `QuestionSet`
- 题集也持有这个题目

这是 JPA 关系映射里非常常见的写法。

---

## 12. 为什么 `save(questionSet)` 就能把题目一起存进去

这是因为 `QuestionSet` 实体里有这段关系定义：

```java
@OneToMany(mappedBy = "questionSet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
private List<InterviewQuestion> questions = new ArrayList<>();
```

这里最关键的是：

```java
cascade = CascadeType.ALL
```

它的意思是：

- 当你保存 `QuestionSet`
- JPA 会把关联的 `InterviewQuestion` 也一起保存

所以这句：

```java
QuestionSet savedQuestionSet = questionSetRepository.save(questionSet);
```

不是只存主表，而是：

- 存 `question_sets`
- 同时存 `interview_questions`

你可以把它理解成：

```text
保存父对象时，把子对象也一起级联保存
```

---

## 13. 第六步：为什么保存完还要转一次 `toDetailResponse()`

保存完成后，代码是：

```java
return toDetailResponse(savedQuestionSet);
```

这一步是把数据库实体再转成接口返回对象。

因为前端真正想要的不是 JPA 实体，而是：

- `QuestionSetDetailResponse`

其中包含：

- 题集主信息
- 题目详情数组

而题目数组里的每一个元素，又会转成：

- `InterviewQuestionResponse`

这里也做了一个格式转换：

```java
splitTags(question.getTags())
```

也就是把数据库里逗号分隔的 `tags` 字符串，再拆回前端更好处理的数组。

---

## 14. `generateQuestionSet()` 可以总结成一条顺口溜

你可以先这样记它：

```text
先补参数
再查岗位
再组命令
再生成题
再转实体
再存数据库
最后转响应
```

这基本就是 `generateQuestionSet()` 的全部逻辑。

---

## 15. 它是怎么调用数据库的

这部分要分两块看。

### 1. 查岗位画像表

`resolveProfile()` 里调用：

```java
positionProfileRepository.findByCodeAndActiveTrue(positionCode.trim())
```

这个方法是 Spring Data JPA 自动根据方法名生成查询。

大致等价于：

```sql
SELECT *
FROM position_profiles
WHERE code = ?
  AND active = true
LIMIT 1;
```

这一步是为了拿到预设岗位的：

- 标题
- 难度
- 默认关注知识点

### 2. 存和查题集表

`QuestionSetRepository` 里有两个核心查询方法：

```java
List<QuestionSet> findAllByOrderByCreatedAtDesc();
Optional<QuestionSet> findById(Long id);
```

#### `findAllByOrderByCreatedAtDesc()`

大致等价于：

```sql
SELECT *
FROM question_sets
ORDER BY created_at DESC;
```

这个给题集列表页使用。

#### `save(questionSet)`

这是 `JpaRepository` 自带方法。

它会根据对象状态决定：

- 是插入新记录
- 还是更新已有记录

在这里因为 `questionSet` 是新建对象，所以会做插入。

#### `findById(id)`

这也是 `JpaRepository` 自带方法。

但这里有一个额外细节：

```java
@EntityGraph(attributePaths = "questions")
Optional<QuestionSet> findById(Long id);
```

这个注解的意思是：

- 查 `QuestionSet` 时
- 把关联的 `questions` 也一并加载出来

这样 `getQuestionSet(id)` 返回详情时，就不会只拿到主表数据，而是连题目子表一起拿到。

---

## 16. `listQuestionSets()` 在做什么

代码结构很简单：

```java
return questionSetRepository.findAllByOrderByCreatedAtDesc()
    .stream()
    .map(questionSet -> new QuestionSetSummaryResponse(...))
    .toList();
```

它做的事情是：

1. 查出所有题集
2. 按创建时间倒序
3. 转成摘要 DTO

注意这里返回的是：

- `QuestionSetSummaryResponse`

不是完整详情。

也就是说列表页不返回每一道题的内容，只返回：

- 题集 ID
- 岗位名
- 难度
- 题目数量
- 来源
- 模型
- 创建时间

这样列表接口更轻。

---

## 17. `getQuestionSet(id)` 在做什么

代码：

```java
QuestionSet questionSet = questionSetRepository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("题集不存在，id=" + id));
return toDetailResponse(questionSet);
```

它做的事情非常直接：

1. 按 `id` 查题集
2. 查不到就抛 `ResourceNotFoundException`
3. 查到了就转成详情响应对象

这里因为 `findById()` 上加了 `@EntityGraph(attributePaths = "questions")`，所以查到的不只是主表，还包括题目子表。

所以详情页能直接拿到：

- 题集基础信息
- 题目数组

---

## 18. `toDetailResponse()` 也很重要

这个方法做的事情是：

- 把数据库实体 `QuestionSet`
- 转成接口响应 `QuestionSetDetailResponse`

并且把子题目也转成：

- `InterviewQuestionResponse`

它不是简单地“把对象原样返回”，而是在做数据格式整理。

例如：

- `question.getTags()` 在数据库里是字符串
- 通过 `splitTags()` 后，返回前端的是数组

这就是 Service 层很典型的职责：

- 数据库结构不一定等于接口结构
- 中间需要一层转换

---

## 19. 这个类为什么有很多小方法

像这些方法：

- `resolveProfile`
- `resolvePositionTitle`
- `resolveDifficulty`
- `resolveFocusAreas`
- `normalizeNullableText`
- `toDetailResponse`
- `splitTags`

它们的存在不是为了“写得花”，而是为了把逻辑拆小。

如果把这些逻辑全塞进 `generateQuestionSet()`，这个方法会非常难读。

拆成小方法的好处：

- 每个方法职责单一
- 更容易定位问题
- 更容易单独理解
- 主流程更清晰

你可以把 `generateQuestionSet()` 看成“导演”，这些小方法是“分工明确的小工种”。

---

## 20. 从数据库表角度怎么理解

这里其实涉及两张主要业务表：

### `question_sets`

保存题集主信息，例如：

- 岗位编码
- 岗位名称
- 难度
- 题目数量
- 自定义要求
- 摘要
- 来源
- 模型
- Prompt 快照
- 原始返回

### `interview_questions`

保存每一道题，例如：

- 题目内容
- 答题思路
- 考察点
- 标签
- 难度
- 顺序
- `question_set_id`

所以生成题集实际上不是只插一张表，而是：

```text
先插一条 question_sets
再插多条 interview_questions
```

只是由于 JPA 级联，你在代码里只写了一次 `save(questionSet)`。

---

## 21. 一个实际例子

比如前端发来：

```json
{
  "positionCode": "ios-mid",
  "questionCount": 4,
  "difficulty": "MID",
  "customRequirements": "重点考察 Swift 并发与内存管理",
  "includeScenarioQuestions": true
}
```

`QuestionSetService` 会做这些事：

1. 用 `positionCode = ios-mid` 去岗位表查 `iOS中级`
2. 补出标题、默认关注点等信息
3. 组装 `GenerationCommand`
4. 调用生成服务拿到 4 道题
5. 生成一个 `QuestionSet`
6. 生成 4 个 `InterviewQuestion`
7. 保存到数据库
8. 返回详情 JSON 给前端

所以你在页面上点一次“生成题集”，背后其实已经完成了一次完整的：

```text
参数整理 -> 岗位查表 -> 题目生成 -> 主从表保存 -> DTO 返回
```

---

## 22. 从 iOS 视角怎么理解它

如果换成 `Swift` 的思维方式，你可以把它类比成：

- `QuestionSetController`
  像网络接口入口

- `QuestionSetService`
  像一个核心 UseCase / Application Service

- `QuestionGenerationService`
  像一个策略协调器

- `QuestionSetRepository`
  像数据库仓储

- `QuestionSet` / `InterviewQuestion`
  像数据库模型

- `QuestionSetDetailResponse`
  像接口给页面的返回模型

也就是说，`QuestionSetService` 最像的是：

```text
一个把“前端请求、数据库、生成器”全串起来的总调度器
```

---

## 23. 一句话总结

`QuestionSetService` 的本质是：

```text
把前端传来的生成请求，
补成一个完整的内部命令，
再调用生成器产出题目，
再把题集和题目一起存进数据库，
最后转成前端能直接展示的响应对象。
```

如果你只先记一句，记这个就够了：

```text
它不是“单纯查库”，而是“生成 + 持久化 + 查询”三合一的核心业务服务。
```
