<template>
  <MainLayout>
    <div class="ai-planner-page">
      <div class="page-header">
        <h1 class="page-title">
          <el-icon><MagicStick /></el-icon>
          AI 智能行程规划
        </h1>
        <p class="page-desc">填写旅行偏好，AI 将为你生成专属旅行计划</p>
      </div>

      <div class="planner-content">
        <!-- Form Card -->
        <el-card class="form-card" shadow="hover">
          <template #header>
            <span class="card-header-title">旅行偏好设置</span>
          </template>

          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-position="top"
            size="large"
            @submit.prevent="handleGenerate"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="出发城市" prop="origin">
                  <el-input
                    v-model="form.origin"
                    placeholder="如：北京、上海..."
                    :prefix-icon="Location"
                  />
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="目的地" prop="destination">
                  <el-input
                    v-model="form.destination"
                    placeholder="如：杭州、三亚..."
                    :prefix-icon="Location"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="旅行天数" prop="days">
                  <el-input-number
                    v-model="form.days"
                    :min="1"
                    :max="30"
                    :step="1"
                    class="full-width"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="预算 (元)" prop="budget">
                  <el-input-number
                    v-model="form.budget"
                    :min="100"
                    :max="100000"
                    :step="100"
                    class="full-width"
                  />
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <el-form-item label="出行人数" prop="travelers">
                  <el-input-number
                    v-model="form.travelers"
                    :min="1"
                    :max="20"
                    :step="1"
                    class="full-width"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="兴趣偏好">
              <el-select
                v-model="form.interests"
                multiple
                placeholder="请选择兴趣偏好（可多选）"
                class="full-width"
              >
                <el-option label="自然风光" value="nature" />
                <el-option label="历史人文" value="culture" />
                <el-option label="美食探索" value="food" />
                <el-option label="购物娱乐" value="shopping" />
                <el-option label="户外运动" value="sports" />
                <el-option label="亲子乐园" value="family" />
                <el-option label="网红打卡" value="landmark" />
                <el-option label="休闲度假" value="leisure" />
              </el-select>
            </el-form-item>

            <el-form-item label="出行场景">
              <el-radio-group v-model="form.scene">
                <el-radio-button value="travel_system">默认</el-radio-button>
                <el-radio-button value="budget_trip">穷游省钱</el-radio-button>
                <el-radio-button value="family_trip">亲子出游</el-radio-button>
                <el-radio-button value="couple_trip">情侣浪漫</el-radio-button>
                <el-radio-button value="photography_trip">摄影打卡</el-radio-button>
                <el-radio-button value="food_trip">美食之旅</el-radio-button>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="出行方式">
              <el-radio-group v-model="form.travelMode">
                <el-radio-button value="driving">自驾</el-radio-button>
                <el-radio-button value="transit">公共交通</el-radio-button>
                <el-radio-button value="walking">步行</el-radio-button>
                <el-radio-button value="mixed">混合</el-radio-button>
              </el-radio-group>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                native-type="submit"
                :loading="generating"
                size="large"
                class="generate-btn"
              >
                <el-icon><MagicStick /></el-icon>
                {{ generating ? 'AI 正在生成行程...' : '生成行程' }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- Result Section -->
        <div v-if="planResult" class="result-section">
          <el-card class="result-card" shadow="hover">
            <template #header>
              <div class="result-header">
                <h2 class="plan-title">{{ planResult.title || '我的旅行计划' }}</h2>
                <el-tag v-if="planResult.budgetTotal" type="warning" size="large">
                  总预算：¥{{ planResult.budgetTotal }}
                </el-tag>
              </div>
            </template>

            <!-- Daily Timeline -->
            <el-timeline v-if="planDays && planDays.length">
              <el-timeline-item
                v-for="(day, index) in planDays"
                :key="day.id || index"
                :timestamp="day.title || `第 ${day.dayNumber || index + 1} 天`"
                placement="top"
                type="primary"
                color="#409eff"
              >
                <el-card shadow="never" class="day-card">
                  <div v-if="day.description" class="day-desc">
                    {{ day.description }}
                  </div>

                  <div v-if="day.routes && day.routes.length" class="routes">
                    <div
                      v-for="(route, rIdx) in day.routes"
                      :key="route.id || rIdx"
                      class="route-item"
                    >
                      <div class="route-info">
                        <span class="route-name">
                          <el-icon color="#409eff"><LocationFilled /></el-icon>
                          {{ route.customName || '行程节点' }}
                          <el-tag
                            v-if="route.nodeType"
                            size="small"
                            class="node-type-tag"
                          >
                            {{ nodeTypeLabel(route.nodeType) }}
                          </el-tag>
                        </span>
                        <span class="route-time" v-if="route.startTime">
                          {{ route.startTime }}{{ route.endTime ? ` - ${route.endTime}` : '' }}
                        </span>
                        <span v-if="route.tips" class="route-tips">
                          {{ route.tips }}
                        </span>
                      </div>
                      <el-tag
                        v-if="route.estimatedCost"
                        type="warning"
                        size="small"
                      >
                        ¥{{ route.estimatedCost }}
                      </el-tag>
                    </div>
                  </div>

                  <div v-else class="no-routes">暂无行程安排</div>
                </el-card>
              </el-timeline-item>
            </el-timeline>

            <!-- Empty state: plan exists but has no days yet -->
            <el-empty
              v-else
              description="行程已生成，暂无每日安排"
            />
          </el-card>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Location } from '@element-plus/icons-vue'
import { generatePlan, getPlanDetail, getPlanDays, getDayRoutes, getProfile } from '../api/index'
import MainLayout from '../layouts/MainLayout.vue'

const route = useRoute()
const formRef = ref(null)
const generating = ref(false)
const planResult = ref(null)
const planDays = ref([])

const form = reactive({
  destination: '',
  origin: '',
  days: 3,
  budget: 3000,
  travelers: 2,
  interests: [],
  scene: 'travel_system',
  travelMode: 'driving',
})

const rules = {
  destination: [
    { required: true, message: '请输入目的地', trigger: 'blur' },
  ],
  days: [
    { required: true, type: 'number', message: '请选择旅行天数', trigger: 'blur' },
  ],
  budget: [
    { required: true, type: 'number', message: '请设置预算', trigger: 'blur' },
  ],
  travelers: [
    { required: true, type: 'number', message: '请选择出行人数', trigger: 'blur' },
  ],
}

const NODE_TYPE_LABELS = {
  attraction: '景点',
  restaurant: '餐厅',
  hotel: '住宿',
  transport: '交通',
  shopping: '购物',
  other: '其他',
}

function nodeTypeLabel(type) {
  return NODE_TYPE_LABELS[type] || type
}

// Only retry on server/network errors, not client errors
function isRetryableError(err) {
  // axios timeout or network error
  if (err.code === 'ECONNABORTED' || err.code === 'ERR_NETWORK') return true
  // HTTP 5xx or connection reset
  const status = err.response?.status
  if (status && status >= 500) return true
  // Business-level 500 returned in response body
  if (err.message?.includes('服务器内部异常')) return true
  return false
}

// Load plan detail + days + routes by planId
async function loadPlanDetail(planId) {
  const detailRes = await getPlanDetail(planId)
  const plan = detailRes.data || detailRes
  planResult.value = plan

  let days = []
  try {
    const daysRes = await getPlanDays(planId)
    days = daysRes.data || daysRes || []
  } catch (err) {
    console.error('获取行程日失败:', err)
    days = []
  }

  // Load routes for each day
  if (Array.isArray(days)) {
    for (const day of days) {
      try {
        const routesRes = await getDayRoutes(planId, day.id)
        day.routes = routesRes.data || routesRes || []
      } catch {
        day.routes = []
      }
    }
  }
  planDays.value = days
}

// Load profile (homeCity) and existing plan via ?planId=xxx
onMounted(async () => {
  // 预填出发城市
  try {
    const res = await getProfile()
    if (res.data?.homeCity) {
      form.origin = res.data.homeCity
    }
  } catch (e) {
    // 未登录或无画像时忽略
  }

  const planId = route.query.planId
  if (planId) {
    generating.value = true
    try {
      await loadPlanDetail(planId)
    } catch (err) {
      console.error('加载攻略失败:', err)
    } finally {
      generating.value = false
    }
  }
})

async function handleGenerate() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  generating.value = true
  planResult.value = null
  planDays.value = []

  // Align with backend API: { destination, days, budget, travelers, interests?, travelMode?, scene? }
  const payload = {
    destination: form.destination,
    days: form.days,
    budget: form.budget,
    travelers: form.travelers,
  }
  if (form.origin) {
    payload.origin = form.origin
  }
  if (form.interests.length > 0) {
    payload.interests = form.interests.join(' ')
  }
  if (form.travelMode) {
    payload.travelMode = form.travelMode
  }
  if (form.scene) {
    payload.scene = form.scene
  }

  // Retry AI generation up to 2 times (intermittent 500s from backend LLM parsing)
  const MAX_RETRIES = 2
  let lastError = null

  for (let attempt = 0; attempt <= MAX_RETRIES; attempt++) {
    try {
      if (attempt > 0) {
        ElMessage.info(`第 ${attempt + 1} 次尝试生成...`)
      }

      const res = await generatePlan(payload)
      // Backend returns { code: 200, data: planId }
      const planId = res.data

      await loadPlanDetail(planId)
      ElMessage.success('行程生成成功！')
      generating.value = false
      return // success, exit
    } catch (err) {
      lastError = err
      console.error(`生成行程失败 (attempt ${attempt + 1}/${MAX_RETRIES + 1}):`, err)

      // Only retry on 500/server errors; don't retry on 400/401
      if (attempt < MAX_RETRIES && isRetryableError(err)) {
        // Brief delay before retry
        await new Promise((r) => setTimeout(r, 2000))
        continue
      }
      break
    }
  }

  // All retries exhausted
  console.error('生成行程失败，已达最大重试次数:', lastError)
  // Error message already shown by request interceptor
  generating.value = false
}
</script>

<style scoped>
.ai-planner-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 8px;
}

.page-desc {
  font-size: 15px;
  color: #999;
}

.planner-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.card-header-title {
  font-size: 16px;
  font-weight: 600;
}

.full-width {
  width: 100%;
}

.generate-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
}

/* Result */
.result-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.plan-title {
  font-size: 20px;
  font-weight: 600;
}

.day-card {
  margin-top: 8px;
  background: #fafafa;
}

.day-desc {
  color: #666;
  font-size: 14px;
  margin-bottom: 12px;
  line-height: 1.6;
}

.routes {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.route-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.route-item:last-child {
  border-bottom: none;
}

.route-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
}

.route-name {
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
}

.node-type-tag {
  margin-left: 2px;
}

.route-time {
  font-size: 12px;
  color: #909399;
  padding-left: 20px;
}

.route-tips {
  font-size: 13px;
  color: #999;
  padding-left: 20px;
  line-height: 1.5;
}

.no-routes {
  color: #999;
  font-size: 14px;
}
</style>
