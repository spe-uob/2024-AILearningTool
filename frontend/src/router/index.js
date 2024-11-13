import { createRouter, createWebHistory } from 'vue-router';
import Cookie from '@/Display interface/Cookie.vue';

const routes = [
  {
    path: '/',
    name: 'Cookie',
    component: Cookie,
  },
  {
    path: '/main',
    name: 'Main',
    component: () => import('@/Display interface/MainView.vue'),
  },
];

const router = createRouter({
  history: createWebHistory('/'),
  routes,
});

export default router;
