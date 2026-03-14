<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { fetchQuestionSetDetail } from '../../api/interview'
import { formatDateTime } from '../../utils/format'

const loading = ref(false)
const detail = ref(null)

onLoad(async (options) => {
  if (!options?.id) {
    uni.showToast({
      title: '缺少题集 ID',
      icon: 'none',
    })
    return
  }

  await loadDetail(options.id)
})

/**
 * 读取题集详情。
 *
 * @param {string} id 题集 ID
 */
async function loadDetail(id) {
  loading.value = true
  try {
    detail.value = await fetchQuestionSetDetail(id)
  } catch (error) {
    uni.showToast({
      title: error.message || '详情加载失败',
      icon: 'none',
    })
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <view class="page-shell">
    <view v-if="loading" class="panel-card state-card">
      <text>正在加载题集详情...</text>
    </view>

    <template v-else-if="detail">
      <view class="summary-card panel-card">
        <text class="summary-title">{{ detail.positionTitle }}</text>
        <text class="summary-meta">
          {{ detail.difficulty }} · {{ detail.questionCount }} 题 · {{ detail.aiModel }}
        </text>
        <text class="summary-copy muted-text">{{ detail.summary }}</text>
        <text v-if="detail.customRequirements" class="summary-extra">
          自定义要求：{{ detail.customRequirements }}
        </text>
        <text class="summary-time muted-text">{{ formatDateTime(detail.createdAt) }}</text>
      </view>

      <view
        v-for="question in detail.questions"
        :key="question.id"
        class="question-card panel-card"
      >
        <text class="question-index">Q{{ question.displayOrder }}</text>
        <text class="question-title">{{ question.question }}</text>
        <view class="tag-row">
          <text
            v-for="tag in question.tags"
            :key="tag"
            class="tag"
          >
            {{ tag }}
          </text>
        </view>
        <text class="answer-label">答题思路</text>
        <text class="answer-body">{{ question.answerIdea }}</text>
      </view>
    </template>
  </view>
</template>

<style scoped lang="scss">
.state-card,
.summary-card,
.question-card {
  padding: 30rpx;
  margin-bottom: 24rpx;
}

.summary-title {
  display: block;
  font-size: 40rpx;
  font-weight: 800;
}

.summary-meta,
.summary-copy,
.summary-extra,
.summary-time {
  display: block;
  margin-top: 14rpx;
  font-size: 27rpx;
  line-height: 1.7;
}

.summary-extra {
  color: #8a5f3f;
}

.question-index {
  display: inline-flex;
  width: 72rpx;
  height: 72rpx;
  border-radius: 24rpx;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #b25b32, #8f3d1e);
  color: #fff;
  font-size: 26rpx;
  font-weight: 700;
}

.question-title {
  display: block;
  margin-top: 20rpx;
  font-size: 32rpx;
  font-weight: 700;
  line-height: 1.5;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 14rpx;
  margin-top: 20rpx;
}

.tag {
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(240, 180, 90, 0.18);
  color: #9b5d2d;
  font-size: 22rpx;
}

.answer-label {
  display: block;
  margin-top: 24rpx;
  font-size: 24rpx;
  color: #8a5f3f;
}

.answer-body {
  display: block;
  margin-top: 12rpx;
  line-height: 1.8;
  font-size: 28rpx;
  white-space: pre-wrap;
}
</style>
