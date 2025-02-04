import { createApp } from 'vue';
import App from './App.vue';
import { createRouter, createWebHistory } from 'vue-router';

const Cookie = () => import('./Display interface/Cookie.vue');
const MainView = () => import('./Display interface/MainView.vue');
const Login = () => import('./components/Login.vue'); 

const routes = [
    { path: '/', component: Cookie, meta: { title: 'Cookie' } },
    { path: '/login', component: Login, meta: { title: 'Login' } }, 
    { path: '/main', component: MainView, meta: { title: 'Chatbot' } },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.afterEach((to) => {
    document.title = to.meta.title || 'Default title';
});

const app = createApp(App);
app.use(router);
app.mount('#app');
