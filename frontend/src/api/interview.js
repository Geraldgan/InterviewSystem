import { request } from './http'

/**
 * 获取岗位画像列表。
 */
export function fetchPositions() {
  return request('/api/positions')
}

/**
 * 创建新的题集。
 *
 * @param {Record<string, unknown>} payload 生成参数
 */
export function generateQuestionSet(payload) {
  return request('/api/question-sets/generate', 'POST', payload)
}

/**
 * 获取题集历史列表。
 */
export function fetchQuestionSets() {
  return request('/api/question-sets')
}

/**
 * 获取单个题集详情。
 *
 * @param {string | number} id 题集 ID
 */
export function fetchQuestionSetDetail(id) {
  return request(`/api/question-sets/${id}`)
}
