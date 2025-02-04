import { createRouter, createWebHistory } from 'vue-router';
import Login from '../Display interface/Login.vue';
import MainView from '../Display interface/MainView.vue';

const routes = [
    { path: '/login', component: Login },
    { path: '/main', component: MainView },
    { path: '/', redirect: '/login' }  
];

router.beforeEach((to, from, next) => {
    const isAuthenticated = localStorage.getItem('token');
    if (to.path !== '/login' && !isAuthenticated) {
        next('/login');
    } else {
        next(); 
    }
});

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
});

export default router;
