<template>
  <view />
</template>

<style lang="scss">
:root {
  --page-bg: #f6efe5;
  --panel-bg: rgba(255, 252, 247, 0.88);
  --panel-border: rgba(93, 72, 49, 0.12);
  --text-primary: #2a2016;
  --text-secondary: #6f5b45;
  --brand: #b25b32;
  --brand-deep: #7a3417;
  --accent: #f0b45a;
  --shadow: 0 18rpx 48rpx rgba(99, 66, 34, 0.12);
}

page {
  background:
    radial-gradient(circle at top left, rgba(240, 180, 90, 0.24), transparent 40%),
    linear-gradient(180deg, #fff8f0 0%, #f4ece2 100%);
  color: var(--text-primary);
  font-family: 'Avenir Next', 'PingFang SC', 'Helvetica Neue', sans-serif;
}

.page-shell {
  min-height: 100vh;
  padding: 32rpx 28rpx 40rpx;
}

.panel-card {
  background: var(--panel-bg);
  border: 1rpx solid var(--panel-border);
  border-radius: 28rpx;
  box-shadow: var(--shadow);
  backdrop-filter: blur(18rpx);
}

.section-title {
  font-size: 34rpx;
  font-weight: 700;
  margin-bottom: 20rpx;
}

.muted-text {
  color: var(--text-secondary);
}

.primary-button {
  height: 92rpx;
  border-radius: 24rpx;
  border: none;
  background: linear-gradient(135deg, var(--brand) 0%, var(--brand-deep) 100%);
  color: #fff8f1;
  font-size: 30rpx;
  font-weight: 700;
}

.ghost-button {
  height: 84rpx;
  border-radius: 24rpx;
  border: 1rpx solid rgba(122, 52, 23, 0.2);
  background: rgba(255, 247, 238, 0.72);
  color: var(--brand-deep);
  font-size: 28rpx;
}
</style>
