import { createApp } from 'vue';
import App from './App.vue';
import { createRouter, createWebHistory } from 'vue-router';
import '@/registerServiceWorker';

const Cookie = () => import('./Display interface/Cookie.vue');
const MainView = () => import('./Display interface/MainView.vue');
const Login = () => import('./Display interface/Login.vue');

const routes = [
    { path: '/', redirect: '/login' },
    { path: '/login', component: Login, meta: { title: 'Login' } },
    { path: '/main', component: MainView, meta: { title: 'Chatbot' } },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

// Authentication check
router.beforeEach((to, from, next) => {
    const isAuthenticated = localStorage.getItem('token');
    if (to.path !== '/login' && !isAuthenticated) {
        next('/login');
    } else {
        next();
    }
});

// Page title update
router.afterEach((to) => {
    document.title = to.meta.title || 'Default title';
});

// Detect if it is a mobile device
function isMobileDevice() {
    return /Android|iPhone|iPad|iPod/i.test(navigator.userAgent);
}

// Attempts to lock screen orientation, performed on mobile only
function lockOrientation() {
    if (!isMobileDevice()) return; // Mobile only

    if (screen.orientation && screen.orientation.lock) {
        screen.orientation.lock("portrait").catch(err => {
            console.warn("Screen orientation lock failed:", err);
        });
    }
}

// Vue mounts and then tries to lock the orientation
document.addEventListener("DOMContentLoaded", lockOrientation);
document.addEventListener("fullscreenchange", lockOrientation);

const app = createApp(App);
app.use(router);
app.mount('#app');

// Try locking again (make sure it takes effect after Vue is mounted)
lockOrientation();

export default router;
