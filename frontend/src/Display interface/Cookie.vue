<template>
  <div class="cookie-container">
    <h1>Cookie Settings</h1>
    <div class="cookie-content">
      <p>
        Please review our cookie settings before continuing to the application.
        You can choose to accept or reject cookies.
      </p>
    </div>
    <div class="button-group">
      <button @click="sendCookieConsent(true)">Accept All Cookies</button>
      <button @click="sendCookieConsent(false)">Reject All Cookies</button>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router';
import axios from 'axios';

export default {
  name: 'Cookie',
  setup() {
    const router = useRouter();

    const sendCookieConsent = async (consent) => {
      try {
        // 使用查询参数发送 true 或 false
        const response = await axios.get('https://ailearningtool.ddns.net:8080/signup');
        console.log('Server response:', response.data);
      } catch (error) {
        console.error('Failed to send cookie consent:', error);
        alert('failed');
      } finally {
        // 无论成功或失败都跳转到主界面
        router.push('/main');
      }
    };

    return { sendCookieConsent };
  },
};
</script>

<style scoped>
.cookie-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  padding: 20px;
  background-color: #f0f0f0;
  text-align: center;
}

.cookie-content {
  margin: 20px 0;
  max-width: 600px;
  font-size: 16px;
  line-height: 1.5;
  color: #333;
}

.button-group {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}

button {
  padding: 10px 20px;
  background-color: #4caf50;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 14px;
}

button:hover {
  background-color: #45a049;
}

button:last-child {
  background-color: #f44336;
}

button:last-child:hover {
  background-color: #e53935;
}
</style>
