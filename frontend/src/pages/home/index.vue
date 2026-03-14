<script setup>
import { computed, reactive, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { fetchPositions, generateQuestionSet } from '../../api/interview'

const positions = ref([])
const loading = ref(false)
const selectedCode = ref('')

const form = reactive({
  positionCode: '',
  positionTitle: '',
  difficulty: 'MID',
  questionCount: 6,
  customRequirements: '',
  focusAreasText: '',
  includeScenarioQuestions: true,
  locale: 'zh-CN',
})

const selectedProfile = computed(() => positions.value.find((item) => item.code === selectedCode.value))

onLoad(async () => {
  await loadPositions()
})

/**
 * 加载岗位画像列表。
 */
async function loadPositions() {
  try {
    positions.value = await fetchPositions()
    if (positions.value.length > 0) {
      applyProfile(positions.value[0])
    }
  } catch (error) {
    uni.showToast({
      title: error.message || '岗位加载失败',
      icon: 'none',
    })
  }
}

/**
 * 将岗位画像内容回填到表单里，减少重复输入。
 *
 * @param {any} profile 岗位画像
 */
function applyProfile(profile) {
  selectedCode.value = profile.code
  form.positionCode = profile.code
  form.positionTitle = profile.title
  form.difficulty = profile.level
  form.focusAreasText = profile.defaultFocusAreas.join('、')
}

/**
 * 解析用户输入的关注知识点。
 */
function buildFocusAreas() {
  return form.focusAreasText
    .split(/[,，、\n]/)
    .map((item) => item.trim())
    .filter(Boolean)
}

/**
 * 调用后端生成题集。
 */
async function handleGenerate() {
  loading.value = true
  try {
    const response = await generateQuestionSet({
      positionCode: form.positionCode || undefined,
      positionTitle: form.positionTitle,
      difficulty: form.difficulty,
      questionCount: Number(form.questionCount),
      customRequirements: form.customRequirements,
      focusAreas: buildFocusAreas(),
      includeScenarioQuestions: form.includeScenarioQuestions,
      locale: form.locale,
    })

    uni.showToast({
      title: '题集已生成',
      icon: 'success',
    })
    uni.navigateTo({
      url: `/pages/detail/index?id=${response.id}`,
    })
  } catch (error) {
    uni.showToast({
      title: error.message || '生成失败',
      icon: 'none',
    })
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <view class="page-shell">
    <view class="hero panel-card">
      <text class="eyebrow">AI Interview Studio</text>
      <text class="headline">针对岗位生成你的专属面试题集</text>
      <text class="muted-text hero-copy">
        前端只负责展示和交互，真正的题目生成逻辑都由 Spring Boot + OpenAI 在服务端完成。
      </text>
    </view>

    <view class="section">
      <text class="section-title">岗位模板</text>
      <scroll-view class="profile-list" scroll-x>
        <view
          v-for="profile in positions"
          :key="profile.code"
          class="profile-card panel-card"
          :class="{ active: selectedCode === profile.code }"
          @tap="applyProfile(profile)"
        >
          <text class="profile-title">{{ profile.title }}</text>
          <text class="profile-meta">{{ profile.category }} · {{ profile.level }}</text>
          <text class="profile-desc muted-text">{{ profile.description }}</text>
        </view>
      </scroll-view>
    </view>

    <view class="section panel-card form-card">
      <text class="section-title">出题参数</text>

      <view class="field">
        <text class="field-label">岗位名称</text>
        <input v-model="form.positionTitle" class="field-input" placeholder="例如：AI Agent 工程师" />
      </view>

      <view class="inline-fields">
        <view class="field compact">
          <text class="field-label">难度</text>
          <input v-model="form.difficulty" class="field-input" placeholder="MID / SENIOR" />
        </view>
        <view class="field compact">
          <text class="field-label">题目数量</text>
          <input v-model="form.questionCount" type="number" class="field-input" />
        </view>
      </view>

      <view class="field">
        <text class="field-label">关注知识点</text>
        <textarea
          v-model="form.focusAreasText"
          class="field-textarea"
          placeholder="使用中文顿号、逗号或换行分隔，例如：Swift 并发、内存管理、架构设计"
        />
      </view>

      <view class="field">
        <text class="field-label">补充要求</text>
        <textarea
          v-model="form.customRequirements"
          class="field-textarea"
          placeholder="例如：重点考察项目实战、线上故障排查、性能优化"
        />
      </view>

      <view class="toggle-row">
        <text>至少包含场景题</text>
        <switch
          color="#B25B32"
          :checked="form.includeScenarioQuestions"
          @change="form.includeScenarioQuestions = $event.detail.value"
        />
      </view>

      <button class="primary-button" :loading="loading" @tap="handleGenerate">
        {{ loading ? '正在生成中...' : '生成题集' }}
      </button>

      <view v-if="selectedProfile" class="helper-card">
        <text class="helper-title">当前模板会默认覆盖这些考点</text>
        <text class="helper-copy">
          {{ selectedProfile.defaultFocusAreas.join(' / ') }}
        </text>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
.hero {
  padding: 36rpx;
  margin-bottom: 28rpx;
  background:
    linear-gradient(135deg, rgba(255, 243, 231, 0.94), rgba(255, 252, 248, 0.9)),
    radial-gradient(circle at top right, rgba(240, 180, 90, 0.18), transparent 40%);
}

.eyebrow {
  display: block;
  font-size: 22rpx;
  letter-spacing: 4rpx;
  color: #9f724e;
  text-transform: uppercase;
}

.headline {
  display: block;
  margin-top: 12rpx;
  font-size: 46rpx;
  line-height: 1.2;
  font-weight: 800;
}

.hero-copy {
  display: block;
  margin-top: 20rpx;
  line-height: 1.6;
  font-size: 28rpx;
}

.section {
  margin-bottom: 28rpx;
}

.profile-list {
  white-space: nowrap;
}

.profile-card {
  display: inline-flex;
  width: 500rpx;
  min-height: 220rpx;
  margin-right: 20rpx;
  padding: 28rpx;
  flex-direction: column;
}

.profile-card.active {
  border-color: rgba(178, 91, 50, 0.32);
  background: rgba(255, 247, 238, 0.96);
}

.profile-title {
  font-size: 32rpx;
  font-weight: 700;
}

.profile-meta {
  margin-top: 12rpx;
  color: #9f724e;
  font-size: 24rpx;
}

.profile-desc {
  margin-top: 18rpx;
  line-height: 1.6;
  font-size: 26rpx;
}

.form-card {
  padding: 30rpx;
}

.field {
  margin-bottom: 24rpx;
}

.inline-fields {
  display: flex;
  gap: 18rpx;
}

.compact {
  flex: 1;
}

.field-label {
  display: block;
  margin-bottom: 12rpx;
  font-size: 26rpx;
  font-weight: 600;
  color: #6c5139;
}

.field-input,
.field-textarea {
  width: 100%;
  box-sizing: border-box;
  border-radius: 22rpx;
  background: rgba(255, 250, 243, 0.92);
  border: 1rpx solid rgba(150, 120, 87, 0.16);
  padding: 24rpx;
  font-size: 28rpx;
  color: #2a2016;
}

.field-textarea {
  min-height: 180rpx;
}

.toggle-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 24rpx;
  color: #6f5b45;
  font-size: 26rpx;
}

.helper-card {
  margin-top: 24rpx;
  padding: 24rpx;
  border-radius: 22rpx;
  background: rgba(255, 245, 231, 0.9);
}

.helper-title {
  display: block;
  font-size: 24rpx;
  color: #8a5f3f;
}

.helper-copy {
  display: block;
  margin-top: 10rpx;
  line-height: 1.6;
  font-size: 26rpx;
}
</style>
