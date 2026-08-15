import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000,
})

// AI generation may take 30~90s, use a separate instance with longer timeout
export const aiRequest = axios.create({
  baseURL: '/api',
  timeout: 120000,
})

// === Shared interceptor logic ===

function attachAuthHeader(config) {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}

function handleUnauthorized(message = '登录已过期，请重新登录') {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  ElMessage.error(message)
  if (router.currentRoute.value.path !== '/login') {
    router.push('/login')
  }
}

function handleBusinessResponse(response) {
  const res = response.data

  if (res && typeof res === 'object' && 'code' in res) {
    if (res.code === 401) {
      handleUnauthorized(res.message || '登录已过期，请重新登录')
      return Promise.reject(new Error(res.message || '未登录'))
    }

    if (res.code === 200) {
      return res
    }

    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  }

  return res
}

function handleHttpError(error) {
  if (error.response) {
    const { status, data } = error.response
    if (status === 401) {
      handleUnauthorized(data?.message || '登录已过期，请重新登录')
    } else {
      ElMessage.error(data?.message || data?.msg || '请求失败')
    }
  } else if (error.code === 'ECONNABORTED') {
    // axios timeout
    ElMessage.error('请求超时，请稍后重试')
  } else {
    ElMessage.error('网络错误，请检查网络连接')
  }
  return Promise.reject(error)
}

// Apply interceptors to both instances
[request, aiRequest].forEach((instance) => {
  instance.interceptors.request.use(attachAuthHeader, (e) => Promise.reject(e))
  instance.interceptors.response.use(handleBusinessResponse, handleHttpError)
})

export default request
