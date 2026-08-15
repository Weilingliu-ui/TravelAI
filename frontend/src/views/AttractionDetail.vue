<template>
  <MainLayout>
    <div class="detail-page" v-loading="loading">
      <div v-if="attraction" class="detail-content">
        <!-- Cover Image -->
        <div class="cover-section">
          <img
            :src="defaultCover"
            :alt="attraction.name"
            class="cover-image"
          />
          <div class="cover-overlay">
            <h1 class="attraction-name">{{ attraction.name }}</h1>
            <div class="attraction-meta">
              <span v-if="cityName" class="meta-item">
                <el-icon><LocationFilled /></el-icon>
                {{ cityName }}
              </span>
              <span v-if="attraction.rating" class="meta-item">
                <el-icon color="#f7ba2a"><StarFilled /></el-icon>
                {{ attraction.rating }}
              </span>
              <span v-if="attraction.visitCount" class="meta-item">
                <el-icon color="#ff6b6b"><View /></el-icon>
                {{ formatVisitCount(attraction.visitCount) }}
              </span>
            </div>
          </div>
        </div>

        <!-- Info Cards -->
        <el-row :gutter="20" class="info-section">
          <el-col :span="16">
            <el-card shadow="hover">
              <template #header>
                <span class="card-title">景点详情</span>
              </template>
              <div class="description">
                {{ attraction.description || '暂无介绍信息' }}
              </div>
              <div v-if="attraction.address" class="detail-item">
                <span class="detail-label">地址：</span>
                <span>{{ attraction.address }}</span>
              </div>
              <div v-if="attraction.openingHours" class="detail-item">
                <span class="detail-label">开放时间：</span>
                <span>{{ attraction.openingHours }}</span>
              </div>
              <div v-if="attraction.ticketPrice !== undefined && attraction.ticketPrice !== null" class="detail-item">
                <span class="detail-label">门票：</span>
                <span>{{ attraction.ticketPrice > 0 ? '¥' + attraction.ticketPrice : '免费' }}</span>
              </div>
              <div v-if="attraction.duration" class="detail-item">
                <span class="detail-label">建议游玩时长：</span>
                <span>{{ attraction.duration }} 分钟</span>
              </div>
            </el-card>
          </el-col>

          <el-col :span="8">
            <el-card shadow="hover">
              <template #header>
                <span class="card-title">标签</span>
              </template>
              <div class="tags-wrapper">
                <el-tag v-if="attraction.category" class="detail-tag" type="primary">
                  {{ categoryLabel(attraction.category) }}
                </el-tag>
                <el-tag v-if="attraction.isFree === 1" class="detail-tag" type="success">
                  免费
                </el-tag>
                <el-tag v-if="attraction.isMustVisit === 1" class="detail-tag" type="warning">
                  必去
                </el-tag>
                <el-tag v-if="attraction.isIndoor === 1" class="detail-tag" type="info">
                  室内
                </el-tag>
                <span v-if="!attraction.category && !attraction.isFree && !attraction.isMustVisit && !attraction.isIndoor" class="no-data">暂无标签</span>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- Back button -->
        <div class="back-section">
          <el-button @click="$router.back()">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <el-button type="primary" @click="$router.push('/ai-planner')">
            <el-icon><MagicStick /></el-icon>
            加入行程规划
          </el-button>
        </div>
      </div>

      <el-empty v-if="!loading && !attraction" description="景点不存在" />
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getAttractionDetail, getCities } from '../api/index'
import MainLayout from '../layouts/MainLayout.vue'

const route = useRoute()
const attraction = ref(null)
const loading = ref(true)
const cityName = ref('')

const defaultCover = 'https://picsum.photos/1200/400?random=cover'

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

function formatVisitCount(val) {
  if (!val) return '0'
  const num = Number(val)
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return String(num)
}

onMounted(async () => {
  const id = route.params.id
  if (!id) {
    loading.value = false
    return
  }

  try {
    const res = await getAttractionDetail(id)
    attraction.value = res.data || res

    // Fetch city name
    if (attraction.value?.cityId) {
      try {
        const citiesRes = await getCities()
        let cities = citiesRes.data || []
        if (!Array.isArray(cities)) cities = cities.records || cities.list || []
        const city = cities.find(c => c.id === attraction.value.cityId)
        if (city) cityName.value = city.name || city.nameEn
      } catch { /* ignore */ }
    }
  } catch (err) {
    console.error('获取景点详情失败:', err)
    attraction.value = null
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.detail-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
}

.cover-section {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 24px;
  height: 350px;
  background: #e0e0e0;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 40px 32px 24px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.65));
  color: #fff;
}

.attraction-name {
  font-size: 30px;
  font-weight: 700;
  margin-bottom: 8px;
}

.attraction-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-title {
  font-weight: 600;
  font-size: 16px;
}

.description {
  line-height: 1.8;
  color: #555;
  margin-bottom: 16px;
}

.detail-item {
  margin-bottom: 10px;
  font-size: 14px;
  color: #666;
}

.detail-label {
  font-weight: 500;
  color: #333;
}

.tags-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.detail-tag {
  margin: 0;
}

.no-data {
  color: #999;
  font-size: 14px;
}

.back-section {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  justify-content: center;
}
</style>
