/**
 * 将 ISO 日期格式化为更适合界面展示的文本。
 *
 * @param {string} value 原始日期
 * @returns {string} 格式化后的日期
 */
export function formatDateTime(value) {
  if (!value) {
    return '暂无时间'
  }

  return value.replace('T', ' ').slice(0, 16)
}
