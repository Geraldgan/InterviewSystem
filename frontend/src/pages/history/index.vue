<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { fetchQuestionSets } from '../../api/interview'
import { formatDateTime } from '../../utils/format'

const loading = ref(false)
const questionSets = ref([])

onShow(async () => {
  await loadQuestionSets()
})

/**
 * 读取历史题集列表。
 */
async function loadQuestionSets() {
  loading.value = true
  try {
    questionSets.value = await fetchQuestionSets()
  } catch (error) {
    uni.showToast({
      title: error.message || '题集加载失败',
      icon: 'none',
    })
  } finally {
    loading.value = false
  }
}

function openDetail(id) {
  uni.navigateTo({
    url: `/pages/detail/index?id=${id}`,
  })
}
</script>

<template>
  <view class="page-shell">
    <view class="hero panel-card">
      <text class="hero-title">历史题集</text>
      <text class="muted-text hero-copy">这里能快速回看你之前为不同岗位生成的题目和答题思路。</text>
    </view>

    <view v-if="loading" class="state-card panel-card">
      <text>正在加载题集...</text>
    </view>

    <view v-else-if="questionSets.length === 0" class="state-card panel-card">
      <text class="state-title">还没有历史记录</text>
      <text class="muted-text">先去“出题”页生成第一套面试题吧。</text>
    </view>

    <view
      v-for="item in questionSets"
      :key="item.id"
      class="history-card panel-card"
      @tap="openDetail(item.id)"
    >
      <view class="history-top">
        <text class="history-title">{{ item.positionTitle }}</text>
        <text class="history-badge">{{ item.source }}</text>
      </view>
      <text class="history-meta">{{ item.difficulty }} · {{ item.questionCount }} 题 · {{ item.aiModel }}</text>
      <text class="history-time muted-text">{{ formatDateTime(item.createdAt) }}</text>
    </view>
  </view>
</template>

<style scoped lang="scss">
.hero,
.state-card,
.history-card {
  padding: 30rpx;
  margin-bottom: 24rpx;
}

.hero-title {
  display: block;
  font-size: 42rpx;
  font-weight: 800;
}

.hero-copy {
  display: block;
  margin-top: 16rpx;
  line-height: 1.6;
  font-size: 28rpx;
}

.state-title {
  display: block;
  margin-bottom: 12rpx;
  font-size: 30rpx;
  font-weight: 700;
}

.history-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 18rpx;
}

.history-title {
  font-size: 32rpx;
  font-weight: 700;
}

.history-badge {
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(240, 180, 90, 0.18);
  color: #9a5a28;
  font-size: 22rpx;
}

.history-meta,
.history-time {
  display: block;
  margin-top: 14rpx;
  font-size: 26rpx;
}
</style>
