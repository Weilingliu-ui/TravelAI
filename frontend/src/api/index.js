import request, { aiRequest } from '../utils/request'

// ==================== Auth ====================
export function login(data) {
  return request.post('/auth/login', data)
}

export function register(data) {
  return request.post('/auth/register', data)
}

// ==================== Profile (auth) ====================
export function getProfile() {
  return request.get('/profile')
}

export function updateHomeCity(homeCity) {
  return request.put('/profile/home-city', { homeCity })
}

// ==================== Basic Data (public) ====================
export function getProvinces(params) {
  return request.get('/provinces', { params })
}

export function getCities(params) {
  return request.get('/cities', { params })
}

export function getAttractions(params) {
  return request.get('/attractions', { params })
}

export function getHotAttractions(limit = 6) {
  return request.get('/attractions/hot', { params: { limit } })
}

export function searchAttractions(keyword) {
  return request.get('/attractions/search', { params: { keyword } })
}

export function getAttractionDetail(id) {
  return request.get(`/attractions/${id}`)
}

// ==================== AI Planner (auth) ====================
// Returns { code: 200, data: planId }
// Uses aiRequest (120s timeout) because LLM generation takes 30~90s
export function generatePlan(data) {
  return aiRequest.post('/ai/generate', data)
}

// Debug: returns candidates + prompt preview (fast, no LLM call)
export function generatePlanDebug(data) {
  return request.post('/ai/travel/debug', data)
}

// ==================== Travel Plans (auth) ====================
export function getMyPlans() {
  return request.get('/travel-plans')
}

export function getPlanDetail(id) {
  return request.get(`/travel-plans/${id}`)
}

export function createPlan(data) {
  return request.post('/travel-plans', data)
}

export function updatePlan(data) {
  return request.put('/travel-plans', data)
}

export function deletePlan(id) {
  return request.delete(`/travel-plans/${id}`)
}

export function getPlanDays(planId) {
  return request.get(`/travel-plans/${planId}/days`)
}

export function createPlanDay(planId, data) {
  return request.post(`/travel-plans/${planId}/days`, data)
}

export function getDayRoutes(planId, dayId) {
  return request.get(`/travel-plans/${planId}/days/${dayId}/routes`)
}

export function createDayRoute(planId, dayId, data) {
  return request.post(`/travel-plans/${planId}/days/${dayId}/routes`, data)
}

// ==================== Favorites (auth) ====================
export function getFavorites() {
  return request.get('/favorites')
}

export function addFavorite(travelPlanId) {
  return request.post(`/favorites/${travelPlanId}`)
}

export function removeFavorite(travelPlanId) {
  return request.delete(`/favorites/${travelPlanId}`)
}

export function getFavoriteStatus(travelPlanId) {
  return request.get(`/favorites/${travelPlanId}/status`)
}

// ==================== Comments (auth) ====================
export function getAttractionComments(attractionId, params) {
  return request.get(`/comments/attraction/${attractionId}`, { params })
}

export function createComment(data) {
  return request.post('/comments', data)
}

export function deleteComment(id) {
  return request.delete(`/comments/${id}`)
}

// ==================== Files ====================
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

// ==================== Prompt Templates (auth) ====================
export function getPromptTemplates(params) {
  return request.get('/prompt-templates', { params })
}

export function createPromptTemplate(data) {
  return request.post('/prompt-templates', data)
}

export function updatePromptTemplate(data) {
  return request.put('/prompt-templates', data)
}

export function deletePromptTemplate(id) {
  return request.delete(`/prompt-templates/${id}`)
}
