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
      <button @click="acceptCookies">Accept All Cookies</button>
      <button @click="rejectCookies">Reject All Cookies</button>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router';
import { signupWithConsent, revokeConsent } from '@/api/CookieAPI';

export default {
  name: 'Cookie',
  setup() {
    const router = useRouter();

    const acceptCookies = async () => {
      try {
        const response = await signupWithConsent();
        console.log('User signed up with consent:', response);
        localStorage.setItem('userConsent', 'accepted');
      } catch (error) {
        console.error('Failed to sign up with consent:', error);
        alert('Failed to accept cookies');
      } finally {
        await router.push('/main');
      }
    };

    const rejectCookies = async () => {
      try {
        const response = await revokeConsent();
        console.log('User consent revoked:', response);
        localStorage.setItem('userConsent', 'rejected');
      } catch (error) {
        console.error('Failed to revoke consent:', error);
        alert('Failed to reject cookies');
      } finally {
        await router.push('/main');
      }
    };

    return { acceptCookies, rejectCookies };
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
