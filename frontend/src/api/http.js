const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

/**
 * 统一封装 uni.request，便于后续给所有端共享。
 *
 * @param {string} url 接口路径
 * @param {import('@dcloudio/types').UniNamespace.RequestOptions['method']} method HTTP 方法
 * @param {Record<string, unknown>=} data 请求体
 * @returns {Promise<any>} 接口返回体
 */
export function request(url, method = 'GET', data) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${API_BASE_URL}${url}`,
      method,
      data,
      success: (response) => {
        if (response.statusCode >= 200 && response.statusCode < 300) {
          resolve(response.data)
          return
        }

        reject(new Error(response.data?.message || '请求失败'))
      },
      fail: (error) => {
        reject(error)
      },
    })
  })
}
