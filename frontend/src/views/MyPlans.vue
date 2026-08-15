<template>
  <MainLayout>
    <div class="my-plans-page">
      <div class="page-header">
        <h1 class="page-title">
          <el-icon><Collection /></el-icon>
          我的攻略
        </h1>
        <p class="page-desc">查看和管理你生成的旅行攻略</p>
      </div>

      <div v-if="loading" class="loading-wrapper">
        <el-skeleton :rows="4" animated />
      </div>

      <div v-else-if="plans.length > 0" class="plans-grid">
        <el-row :gutter="20">
          <el-col
            v-for="plan in plans"
            :key="plan.id"
            :xs="24"
            :sm="12"
            :md="8"
          >
            <el-card class="plan-card" shadow="hover">
              <div class="plan-card-header">
                <h3 class="plan-card-title">
                  {{ plan.title || '未命名攻略' }}
                </h3>
                <el-tag v-if="plan.budgetTotal" type="warning" size="small">
                  ¥{{ plan.budgetTotal }}
                </el-tag>
              </div>

              <div class="plan-card-body">
                <div class="plan-info-item" v-if="cityName(plan.destinationCityId)">
                  <el-icon><LocationFilled /></el-icon>
                  <span>{{ cityName(plan.destinationCityId) }}</span>
                </div>
                <div class="plan-info-item" v-if="plan.days">
                  <el-icon><Calendar /></el-icon>
                  <span>{{ plan.days }} 天</span>
                </div>
                <div class="plan-info-item" v-if="plan.travelStyle">
                  <el-icon><Van /></el-icon>
                  <span>{{ styleLabel(plan.travelStyle) }}</span>
                </div>
                <div class="plan-info-item" v-if="plan.createdAt || plan.createTime">
                  <el-icon><Clock /></el-icon>
                  <span>{{ formatDate(plan.createdAt || plan.createTime) }}</span>
                </div>
              </div>

              <div class="plan-card-footer">
                <el-button type="primary" text @click="viewPlan(plan.id)">
                  查看详情
                </el-button>
                <el-button type="danger" text @click="handleDelete(plan)">
                  删除
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <el-empty
        v-else
        description="还没有生成攻略，快去试试 AI 规划吧！"
      >
        <el-button type="primary" @click="$router.push('/ai-planner')">
          <el-icon><MagicStick /></el-icon>
          立即规划
        </el-button>
      </el-empty>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyPlans, deletePlan, getCities } from '../api/index'
import MainLayout from '../layouts/MainLayout.vue'

const router = useRouter()
const plans = ref([])
const loading = ref(true)
const cityMap = ref({})

const STYLE_LABELS = {
  culture: '人文历史',
  nature: '自然风光',
  leisure: '休闲度假',
  food: '美食之旅',
  adventure: '户外冒险',
  family: '亲子游',
  shopping: '购物之旅',
  photography: '摄影之旅',
  budget: '穷游',
  couple: '情侣游',
}

function styleLabel(style) {
  return STYLE_LABELS[style] || style || ''
}

function cityName(cityId) {
  if (cityId == null) return ''
  return cityMap.value[cityId] || `城市#${cityId}`
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return dateStr
  return d.toLocaleDateString('zh-CN')
}

onMounted(async () => {
  // Load city map for destinationCityId -> name
  try {
    const citiesRes = await getCities()
    let cities = citiesRes.data || []
    if (!Array.isArray(cities)) cities = cities.records || cities.list || []
    cities.forEach(c => { cityMap.value[c.id] = c.name || c.nameEn })
  } catch {
    // city map optional
  }

  try {
    const res = await getMyPlans()
    // Backend: { code: 200, data: [...] }
    plans.value = res.data || res || []
    if (!Array.isArray(plans.value)) {
      plans.value = []
    }
  } catch (err) {
    console.error('获取攻略列表失败:', err)
    plans.value = []
  } finally {
    loading.value = false
  }
})

function viewPlan(id) {
  if (id) {
    router.push(`/ai-planner?planId=${id}`)
  }
}

async function handleDelete(plan) {
  try {
    await ElMessageBox.confirm(
      `确定要删除攻略「${plan.title || '未命名'}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    try {
      await deletePlan(plan.id)
      plans.value = plans.value.filter(p => p.id !== plan.id)
      ElMessage.success('删除成功')
    } catch (err) {
      console.error('删除失败:', err)
      // Error message already shown by interceptor
    }
  } catch {
    // User cancelled
  }
}
</script>

<style scoped>
.my-plans-page {
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

.loading-wrapper {
  padding: 20px;
}

.plan-card {
  margin-bottom: 20px;
  border-radius: 12px;
  transition: transform 0.3s;
}

.plan-card:hover {
  transform: translateY(-4px);
}

.plan-card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 12px;
  gap: 8px;
}

.plan-card-title {
  font-size: 17px;
  font-weight: 600;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.plan-card-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.plan-info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
}

.plan-card-footer {
  display: flex;
  justify-content: flex-end;
  gap: 4px;
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
}
</style>
