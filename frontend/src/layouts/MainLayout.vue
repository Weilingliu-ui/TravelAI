<template>
  <div class="main-layout">
    <!-- Top Navigation -->
    <el-header class="app-header">
      <div class="header-left">
        <router-link to="/" class="logo">
          <span class="logo-text">TravelAI</span>
        </router-link>
      </div>
      <div class="header-right">
        <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          :ellipsis="false"
          router
          class="header-menu"
        >
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/ai-planner">
            <el-icon><MagicStick /></el-icon>
            <span>AI 行程规划</span>
          </el-menu-item>
          <el-menu-item index="/my-plans">
            <el-icon><Collection /></el-icon>
            <span>我的攻略</span>
          </el-menu-item>
        </el-menu>
        <div class="user-area">
          <template v-if="authStore.isLoggedIn">
            <span class="username">{{ authStore.username }}</span>
            <el-button type="danger" text @click="handleLogout">退出</el-button>
          </template>
          <template v-else>
            <el-button type="primary" @click="$router.push('/login')">登录</el-button>
          </template>
        </div>
      </div>
    </el-header>

    <!-- Main Content -->
    <el-main class="app-main">
      <slot />
    </el-main>

    <!-- Footer -->
    <el-footer class="app-footer">
      <p>&copy; 2026 TravelAI - AI 智能旅游攻略平台</p>
    </el-footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const authStore = useAuthStore()

const activeMenu = computed(() => {
  const path = route.path
  if (path === '/ai-planner') return '/ai-planner'
  if (path === '/my-plans') return '/my-plans'
  return '/'
})

function handleLogout() {
  authStore.logout()
  window.location.href = '/'
}
</script>

<style scoped>
.main-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 0 24px;
  height: 60px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left .logo-text {
  font-size: 22px;
  font-weight: 700;
  color: #409eff;
  letter-spacing: 1px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-menu {
  border-bottom: none !important;
}

.header-menu .el-menu-item {
  border-bottom: 2px solid transparent;
}

.header-menu .el-menu-item.is-active {
  border-bottom-color: #409eff;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 8px;
}

.username {
  font-size: 14px;
  color: #666;
}

.app-main {
  flex: 1;
}

.app-footer {
  text-align: center;
  padding: 20px;
  background: #fff;
  color: #999;
  font-size: 13px;
  border-top: 1px solid #eee;
}
</style>
