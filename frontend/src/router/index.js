import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
  },
  {
    path: '/ai-planner',
    name: 'AiPlanner',
    component: () => import('../views/AiPlanner.vue'),
  },
  {
    path: '/attraction/:id',
    name: 'AttractionDetail',
    component: () => import('../views/AttractionDetail.vue'),
  },
  {
    path: '/my-plans',
    name: 'MyPlans',
    component: () => import('../views/MyPlans.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
