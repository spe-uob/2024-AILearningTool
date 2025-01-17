import { createRouter, createWebHistory } from 'vue-router';
import MainView from '../components/MainView.vue';
import Cookie from '../Display interface/Cookie.vue';

const routes = [
    { path: '/', component: Cookie },
    { path: '/main', component: MainView },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
