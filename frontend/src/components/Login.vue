<template>
  <div class="cookie-consent">
    <h2>Cookie Consent</h2>
    <p>We use cookies to improve your experience. Please consent to continue using our site.</p>
    <div class="button-group">
      <button @click="handleConsent(true)">Accept</button>
      <button @click="handleConsent(false)">Decline</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  methods: {
    async handleConsent(isAccepted) {
      try {
        const response = await axios.post('api', {
          consent: isAccepted
        });

        if (response.data.success) {
          // Store consent status locally to prevent showing again
          localStorage.setItem('cookieConsent', isAccepted ? 'accepted' : 'declined');

          // Emit to parent that consent has been handled
          this.$emit('consent-given');
        } else {
          alert('Failed to save consent. Please try again.');
        }
      } catch (error) {
        console.error('Error sending consent:', error);
        alert('An error occurred while sending consent. Please try again.');
      }
    }
  }
};
</script>

<style scoped>
.cookie-consent {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: #f0f0f0;
  text-align: center;
  padding: 20px;
}

.button-group {
  margin-top: 20px;
}

button {
  margin: 0 10px;
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}
</style>
