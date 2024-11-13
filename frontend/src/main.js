import { createApp } from 'vue';
import App from './App.vue';
import { createRouter, createWebHistory } from 'vue-router';

import Cookie from './Display interface/Cookie.vue';
import MainView from './Display interface/MainView.vue'; // 包含主界面所有部分

const routes = [
    { path: '/', component: Cookie }, // 初始路径加载 Cookie 页面
    { path: '/main', component: MainView }, // 主界面
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

const app = createApp(App);
app.use(router);
app.mount('#app');
