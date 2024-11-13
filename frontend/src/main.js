import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { fetchUserConversation } from './api/CookieAPI';

const app = createApp(App);

const userConsent = localStorage.getItem('userConsent');
const userId = 'unique-user-id'; // User ID, which can be obtained from the login system

if (userConsent === 'accepted') {
    // User accepts cookies, loads dialogue logs
    fetchUserConversation(userId)
        .then(response => {
            console.log('User conversation:', response.data);
            // Storing dialogue content to the application's global state or other components
        })
        .catch(error => {
            console.error('Failed to load conversation:', error);
        });
}

app.use(router).mount('#app');
