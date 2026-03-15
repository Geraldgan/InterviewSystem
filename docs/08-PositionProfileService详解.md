# PositionProfileService 详解

这份文档专门解释：

- `PositionProfileService` 这个类到底在做什么
- 它的方法为什么这么写
- 它是怎么“调用数据库”的
- 它和 `Controller / Repository / Entity / DTO` 的关系是什么

如果你是从 `iOS + SwiftUI` 视角来看，可以先把它理解成：

- `Controller` 像接口入口
- `Service` 像业务层
- `Repository` 像数据库访问层
- `Entity` 像数据库表对应的数据模型
- `DTO / Response` 像接口返回给前端的结构体

---

## 1. 先看这条调用链

当前岗位列表接口是：

```text
GET /api/positions
```

一个请求进来以后，会按下面这条链路走：

```text
浏览器 / 前端
    -> PositionProfileController
    -> PositionProfileService
    -> PositionProfileRepository
    -> MySQL 的 position_profiles 表
    -> PositionProfileService 转成 PositionProfileResponse
    -> 返回 JSON 给前端
```

也就是说：

- `Controller` 负责接 HTTP 请求
- `Service` 负责组织业务逻辑
- `Repository` 负责查数据库
- `Service` 再把数据库结果整理成前端更适合使用的格式

---

## 2. Controller 做了什么

文件：

- `backend/src/main/java/com/interview/backend/controller/PositionProfileController.java`

核心代码：

```java
@RestController
@RequestMapping("/api/positions")
public class PositionProfileController {

    private final PositionProfileService positionProfileService;

    public PositionProfileController(PositionProfileService positionProfileService) {
        this.positionProfileService = positionProfileService;
    }

    @GetMapping
    public List<PositionProfileResponse> listProfiles() {
        return positionProfileService.listAvailableProfiles();
    }
}
```

这段代码非常简单，重点只有一件事：

- 当收到 `GET /api/positions` 时，调用 `positionProfileService.listAvailableProfiles()`

所以 `Controller` 本身几乎不做业务处理，它只是把请求转发给 `Service`。

你可以把它理解成：

- 前端问：“把岗位列表给我”
- Controller 回头找 Service：“你去准备一份岗位列表”

---

## 3. Service 做了什么

文件：

- `backend/src/main/java/com/interview/backend/service/PositionProfileService.java`

完整核心逻辑：

```java
@Service
public class PositionProfileService {

    private final PositionProfileRepository positionProfileRepository;

    public PositionProfileService(PositionProfileRepository positionProfileRepository) {
        this.positionProfileRepository = positionProfileRepository;
    }

    public List<PositionProfileResponse> listAvailableProfiles() {
        return positionProfileRepository.findAllByActiveTrueOrderByTitleAsc()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private PositionProfileResponse toResponse(PositionProfile profile) {
        return new PositionProfileResponse(
            profile.getId(),
            profile.getCode(),
            profile.getTitle(),
            profile.getCategory(),
            profile.getLevel(),
            profile.getDescription(),
            splitTags(profile.getDefaultFocusAreas())
        );
    }

    private List<String> splitTags(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return List.of();
        }
        return Arrays.stream(rawValue.split(","))
            .map(String::trim)
            .filter(value -> !value.isBlank())
            .toList();
    }
}
```

---

## 4. `listAvailableProfiles()` 逐步拆开看

这个方法最核心：

```java
public List<PositionProfileResponse> listAvailableProfiles() {
    return positionProfileRepository.findAllByActiveTrueOrderByTitleAsc()
        .stream()
        .map(this::toResponse)
        .toList();
}
```

它可以拆成 3 步。

### 第 1 步：查数据库

```java
positionProfileRepository.findAllByActiveTrueOrderByTitleAsc()
```

这一步返回的是：

```java
List<PositionProfile>
```

也就是：

- 从数据库里查出多条岗位记录
- 每一条记录都映射成一个 `PositionProfile` 对象

这里查到的是“实体对象”，不是最终返回给前端的 JSON。

### 第 2 步：转成流

```java
.stream()
```

这一步只是把 `List<PositionProfile>` 转成流，方便对每个元素做统一处理。

你可以先把它理解成：

- “我要把列表里的每一项都加工一下”

### 第 3 步：逐个转换成返回对象

```java
.map(this::toResponse)
.toList();
```

这一步的含义是：

- 对每一个 `PositionProfile`
- 调用一次 `toResponse(profile)`
- 最后收集成新的 `List<PositionProfileResponse>`

所以这个方法整体就是：

```text
数据库实体列表
-> 转换
-> 前端响应列表
```

---

## 5. `toResponse()` 为什么要单独写

方法如下：

```java
private PositionProfileResponse toResponse(PositionProfile profile) {
    return new PositionProfileResponse(
        profile.getId(),
        profile.getCode(),
        profile.getTitle(),
        profile.getCategory(),
        profile.getLevel(),
        profile.getDescription(),
        splitTags(profile.getDefaultFocusAreas())
    );
}
```

这里做的事情是：

- 把数据库实体 `PositionProfile`
- 转成接口响应对象 `PositionProfileResponse`

为什么不直接把 `PositionProfile` 返回给前端？

因为通常我们不希望数据库实体直接暴露给前端，原因包括：

- 实体是数据库视角的数据结构
- 接口返回通常是前端视角的数据结构
- 后续如果数据库字段调整，不一定希望接口也跟着暴露变化

这里最明显的一个转换点是：

```java
splitTags(profile.getDefaultFocusAreas())
```

数据库里存的是一个字符串：

```text
Swift,内存管理,RunLoop,多线程,网络层设计,性能优化
```

但前端更适合拿数组：

```json
["Swift", "内存管理", "RunLoop", "多线程", "网络层设计", "性能优化"]
```

所以这里不能直接原样返回，需要做格式转换。

---

## 6. `splitTags()` 到底干了什么

代码：

```java
private List<String> splitTags(String rawValue) {
    if (rawValue == null || rawValue.isBlank()) {
        return List.of();
    }
    return Arrays.stream(rawValue.split(","))
        .map(String::trim)
        .filter(value -> !value.isBlank())
        .toList();
}
```

这段逻辑可以按顺序理解：

### 1. 先判断空值

```java
if (rawValue == null || rawValue.isBlank()) {
    return List.of();
}
```

如果数据库里这个字段为空，就直接返回空数组，而不是报错。

### 2. 按逗号拆开

```java
rawValue.split(",")
```

比如：

```text
Swift,内存管理,RunLoop
```

会拆成：

```text
["Swift", "内存管理", "RunLoop"]
```

### 3. 去掉前后空格

```java
.map(String::trim)
```

避免数据库里如果写成：

```text
Swift, 内存管理, RunLoop
```

拆出来有多余空格。

### 4. 过滤空字符串

```java
.filter(value -> !value.isBlank())
```

防止出现这种脏数据：

```text
Swift,,RunLoop,
```

最后就只保留真正有内容的项。

---

## 7. 它到底怎么“调用数据库”

这是最容易让人困惑的一点。

文件：

- `backend/src/main/java/com/interview/backend/repository/PositionProfileRepository.java`

代码：

```java
public interface PositionProfileRepository extends JpaRepository<PositionProfile, Long> {

    List<PositionProfile> findAllByActiveTrueOrderByTitleAsc();

    Optional<PositionProfile> findByCodeAndActiveTrue(String code);
}
```

你可能会问：

> 这里没有 SQL，它到底怎么查数据库？

答案是：

- 它使用的是 `Spring Data JPA`
- Spring 会根据方法名自动生成查询语句

也就是说，这不是你手写 SQL，而是“方法名即查询规则”。

### 这个方法名怎么读

```java
findAllByActiveTrueOrderByTitleAsc
```

拆开就是：

- `findAll`
  查多条

- `ByActiveTrue`
  条件是 `active = true`

- `OrderByTitleAsc`
  按 `title` 升序排列

它大致等价于：

```sql
SELECT *
FROM position_profiles
WHERE active = true
ORDER BY title ASC;
```

所以数据库查询确实发生了，只是 SQL 由 Spring 在运行时自动帮你生成了。

---

## 8. Repository 为什么知道查哪张表

因为它绑定的是这个实体：

```java
JpaRepository<PositionProfile, Long>
```

这里的意思是：

- 管理的实体类型是 `PositionProfile`
- 主键类型是 `Long`

而 `PositionProfile` 这个实体上写了：

```java
@Entity
@Table(name = "position_profiles")
public class PositionProfile extends BaseEntity
```

这就告诉 JPA：

- 这是一个数据库实体
- 它对应的表名是 `position_profiles`

所以整条映射链是：

```text
PositionProfileRepository
-> PositionProfile 实体
-> position_profiles 表
```

---

## 9. `PositionProfile` 实体对应数据库的哪些列

文件：

- `backend/src/main/java/com/interview/backend/entity/PositionProfile.java`

主要字段：

```java
private String code;
private String title;
private String category;
private String level;
private String description;
private String defaultFocusAreas;
private boolean active = true;
```

它们大致会映射成表里的这些列：

- `code`
- `title`
- `category`
- `level`
- `description`
- `default_focus_areas`
- `active`

同时它还继承了 `BaseEntity`，所以表里还有：

- `id`
- `created_at`
- `updated_at`

也就是说，`position_profiles` 这张表不仅有岗位信息，还有公共主键和时间字段。

---

## 10. 当前数据库里实际存了什么

当前数据库里这张表的内容大致是：

```text
id  code               title           category  level   active
3   ai-agent-engineer  AI Agent工程师  AI应用     SENIOR  true
1   ios-mid            iOS中级         移动端     MID     true
2   java-backend       Java后端        服务端     MID     true
4   frontend-mid       前端中级        Web       MID     true
```

比如 `ios-mid` 这一行还会有：

```text
default_focus_areas = Swift,内存管理,RunLoop,多线程,网络层设计,性能优化
```

Service 查出来以后，会把它拆成数组再返回给前端。

---

## 11. 这些数据是从哪来的

文件：

- `backend/src/main/java/com/interview/backend/service/SeedDataInitializer.java`

这个类会在应用启动时执行。

关键逻辑：

```java
if (positionProfileRepository.count() > 0) {
    return;
}
```

意思是：

- 如果表里已经有数据了，就不重复插入

如果表是空的，就执行：

```java
positionProfileRepository.saveAll(List.of(...))
```

插入几条默认岗位数据。

所以这些岗位不是前端传来的，也不是手工 SQL 插进去的，而是应用第一次启动时自动写入数据库的。

---

## 12. 返回给前端的对象长什么样

文件：

- `backend/src/main/java/com/interview/backend/dto/PositionProfileResponse.java`

代码：

```java
public record PositionProfileResponse(
    Long id,
    String code,
    String title,
    String category,
    String level,
    String description,
    List<String> defaultFocusAreas
) {
}
```

注意这里的 `defaultFocusAreas` 已经是：

```java
List<String>
```

所以前端收到的 JSON 会像这样：

```json
[
  {
    "id": 1,
    "code": "ios-mid",
    "title": "iOS中级",
    "category": "移动端",
    "level": "MID",
    "description": "重点考察 Swift / Objective-C、性能优化、架构与实际项目经验。",
    "defaultFocusAreas": [
      "Swift",
      "内存管理",
      "RunLoop",
      "多线程",
      "网络层设计",
      "性能优化"
    ]
  }
]
```

---

## 13. 另一个 Repository 方法也很重要

除了列表查询，这个 Repository 还有另一个方法：

```java
Optional<PositionProfile> findByCodeAndActiveTrue(String code);
```

它在：

- `backend/src/main/java/com/interview/backend/service/QuestionSetService.java`

里被调用。

用途是：

- 当前端传来 `positionCode`
- 后端根据岗位编码查出那一条岗位画像
- 再用这个岗位画像的标题、难度、默认关注点去生成题集

这个方法大致等价于：

```sql
SELECT *
FROM position_profiles
WHERE code = ?
  AND active = true
LIMIT 1;
```

所以你可以把两个查询方法这样记：

- `findAllByActiveTrueOrderByTitleAsc()`
  用来查岗位列表

- `findByCodeAndActiveTrue(code)`
  用来按岗位编码查单条岗位

---

## 14. 如果把它翻译成 iOS 视角

如果你更熟悉 `SwiftUI` / `iOS`，可以这样类比：

- `Controller`
  像你的网络接口入口

- `Service`
  像一个 `UseCase` 或业务层对象

- `Repository`
  像一个数据库访问封装层

- `Entity`
  像数据库版本的 Model

- `Response DTO`
  像接口返回给页面使用的 ViewModel / ResponseModel

这里 `PositionProfileService` 的职责就像：

```text
先去数据库层拿岗位数据
再整理格式
最后给接口层返回前端友好的结果
```

---

## 15. 一句话总结

`PositionProfileService` 不直接写 SQL，也不直接处理 HTTP。

它真正做的是：

1. 调用 `PositionProfileRepository` 查 `position_profiles` 表
2. 只拿 `active = true` 的岗位
3. 按标题排序
4. 把数据库实体 `PositionProfile`
   转成接口响应 `PositionProfileResponse`
5. 把逗号字符串 `defaultFocusAreas`
   转成前端更好用的数组

所以它是一个典型的“业务层 + 数据格式转换层”。

---

## 16. 最后用最短的话再记一遍

你只要先记住这一句就够了：

```text
Controller 收请求
Service 调 Repository 查数据库
Service 把实体转成 Response
最后返回 JSON
```

而 `PositionProfileService.listAvailableProfiles()` 做的就是这件事。
