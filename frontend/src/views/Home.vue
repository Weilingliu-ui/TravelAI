<template>
  <MainLayout>
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-content">
        <h1 class="hero-title">AI 智能旅游攻略平台</h1>
        <p class="hero-subtitle">输入目的地、天数和预算，30 秒生成专属旅行计划</p>
        <el-button
          type="primary"
          size="large"
          round
          class="hero-btn"
          @click="$router.push('/ai-planner')"
        >
          <el-icon><MagicStick /></el-icon>
          立即规划
        </el-button>
      </div>
    </section>

    <!-- Hot Attractions Section -->
    <section class="section attractions-section">
      <div class="section-header">
        <h2 class="section-title">热门景点</h2>
        <p class="section-desc">探索最受欢迎的旅行目的地</p>
      </div>

      <div v-if="loading" class="loading-wrapper">
        <el-skeleton :rows="3" animated />
      </div>

      <el-row v-else :gutter="20" class="attractions-grid">
        <el-col
          v-for="item in attractions"
          :key="item.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="8"
        >
          <el-card
            class="attraction-card"
            shadow="hover"
            :body-style="{ padding: '0' }"
            @click="$router.push(`/attraction/${item.id}`)"
          >
            <div class="card-image-wrapper">
              <img
                :src="getDefaultImage(item.id)"
                :alt="item.name"
                class="card-image"
              />
              <div class="card-city-tag">{{ getCityName(item.cityId) }}</div>
            </div>

            <div class="card-body">
              <h3 class="card-title">{{ item.name }}</h3>
              <div class="card-tags">
                <el-tag size="small" class="tag-item" v-if="item.category">
                  {{ categoryLabel(item.category) }}
                </el-tag>
              </div>
              <div class="card-stats">
                <div class="stat-item">
                  <el-icon color="#f7ba2a"><StarFilled /></el-icon>
                  <span>{{ item.rating || '暂无评分' }}</span>
                </div>
                <div class="stat-item">
                  <el-icon color="#409eff"><View /></el-icon>
                  <span>{{ formatVisitCount(item.visitCount) }}</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-empty v-if="!loading && attractions.length === 0" description="暂无热门景点" />
    </section>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHotAttractions, getCities } from '../api/index'
import MainLayout from '../layouts/MainLayout.vue'

const attractions = ref([])
const loading = ref(true)
const cityMap = ref({})

const CATEGORY_LABELS = {
  nature: '自然风光',
  culture: '历史人文',
  food: '美食探索',
  shopping: '购物娱乐',
  sports: '户外运动',
  entertainment: '休闲娱乐',
  landmark: '地标打卡',
}

function categoryLabel(cat) {
  return CATEGORY_LABELS[cat] || cat
}

onMounted(async () => {
  try {
    // Fetch cities and hot attractions in parallel
    const [attRes, citiesRes] = await Promise.all([
      getHotAttractions(6),
      getCities().catch(() => ({ data: [] })),
    ])

    // Build city ID -> name map
    let cities = citiesRes.data || []
    if (!Array.isArray(cities)) cities = cities.records || cities.list || []
    cities.forEach(c => { cityMap.value[c.id] = c.name || c.nameEn })

    // Handle response structure: res = {code, message, data: [...]}
    attractions.value = attRes.data || attRes.rows || attRes.list || attRes || []
    if (!Array.isArray(attractions.value)) {
      attractions.value = []
    }
  } catch (err) {
    console.error('获取热门景点失败:', err)
    attractions.value = []
  } finally {
    loading.value = false
  }
})

function getCityName(cityId) {
  return cityMap.value[cityId] || `城市#${cityId}`
}

function formatVisitCount(val) {
  if (!val) return '0'
  const num = Number(val)
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return String(num)
}

function getDefaultImage(seed) {
  return 'https://picsum.photos/400/240?random=' + seed
}
</script>

<style scoped>
/* Hero */
.hero {
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  color: #fff;
  padding: 80px 24px;
  text-align: center;
}

.hero-title {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 16px;
  letter-spacing: 2px;
}

.hero-subtitle {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 32px;
}

.hero-btn {
  font-size: 18px;
  padding: 14px 40px;
  height: auto;
}

/* Section */
.section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 48px 24px;
}

.section-header {
  text-align: center;
  margin-bottom: 36px;
}

.section-title {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.section-desc {
  font-size: 15px;
  color: #999;
}

/* Cards */
.attraction-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
  border-radius: 12px;
  overflow: hidden;
}

.attraction-card:hover {
  transform: translateY(-4px);
}

.card-image-wrapper {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f0f0f0;
}

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-city-tag {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.55);
  color: #fff;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
}

.card-body {
  padding: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-tags {
  display: flex;
  gap: 6px;
  margin-bottom: 10px;
  flex-wrap: wrap;
  min-height: 24px;
}

.tag-item {
  margin: 0;
}

.card-stats {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #666;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.loading-wrapper {
  padding: 20px;
}
</style>
