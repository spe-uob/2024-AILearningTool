<template>
  <div class="cookie-popup">
    <div class="cookie-content">
      <p>
        Watsonx AI uses cookies serving systems and analytics. If you continue to use this site, you consent to
        the collection and storage on your local machine.
      </p>
      <button class="accept-button" @click="handleConsent(true)">Allow all cookies</button>
    </div>
  </div>
</template>

<script>
export default {
  methods: {
    handleConsent(isConsent) {
      this.setConsentCookie(isConsent);
      this.$emit("consent-choice", isConsent); // 只通知父组件，不再调用 signUp()
    },

    setConsentCookie(isConsent) {
      const consentValue = isConsent ? "true" : "false";
      const d = new Date();
      d.setTime(d.getTime() + 30 * 24 * 60 * 60 * 1000); // 30天过期
      const expires = "expires=" + d.toUTCString();
      document.cookie = `optionalConsent=${consentValue};${expires};path=/`;
    }
  }
};
</script>

<style scoped>
.cookie-popup {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background-color: transparent;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
}

.cookie-content {
  width: 90%;
  max-width: 500px;
  background-color: #fff;
  padding: 20px;
  border-radius: 10px 10px 0 0;
  text-align: center;
  box-shadow: 0 -4px 8px rgba(0, 0, 0, 0.1);
}

.cookie-content p {
  font-size: 16px;
  margin-bottom: 20px;
}

button {
  font-size: 16px;
  padding: 12px 24px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  color: white;
  width: 100%;
}

.reject-button {
  background-color: #ccc;
  margin-top: 10px;
}

.reject-button:hover {
  background-color: #bbb;
}

.accept-button {
  background-color: #5C88DA;
}

.accept-button:hover {
  background-color: #5C88DA;
}
</style>
