import { createRouter, createWebHistory } from 'vue-router';
import Cookie from '@/DisplayInterface/Cookie.vue';

const routes = [
  {
    path: '/',
    name: 'Cookie',
    component: Cookie,
  },
  {
    path: '/main',
    name: 'Main',
    component: () => import('@/DisplayInterface/MainView.vue'),
  },
];

const router = createRouter({
  history: createWebHistory('/'),
  routes,
});

export default router;