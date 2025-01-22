<template>
  <div id="cookiePopUp" class="cookie-popup">
    <div class="cookie-content">
      <p>We use cookies to optimize your experience. Do you agree to optional cookies?</p>
      <button class="reject-button" @click="handleConsent(false)">Refuse partial cookies</button>
      <button class="accept-button" @click="handleConsent(true)">Allow all cookies</button>
    </div>
  </div>
</template>

<script>
export default {
  methods: {
    handleConsent(isConsent) {
      this.setConsentCookie(isConsent);
      this.signUp();
    },
    setConsentCookie(isConsent) {
      const consentValue = isConsent ? "true" : "false";
      const d = new Date();
      d.setTime(d.getTime() + 30 * 24 * 60 * 60 * 1000);
      const expires = "expires=" + d.toUTCString();
      document.cookie = `optionalConsent=${consentValue};${expires};path=/`;
    },
    signUp() {
      fetch("https://ailearningtool.ddns.net:8080", {
        method: "GET",
        credentials: "include",
      })
          .then((response) => {
            if (!response.ok) {
              throw new Error("Non-200 response, back-end API call failed");
            } else {
              document.getElementById("cookiePopUp").style.display = "none";
              this.redirectToMain();
            }
          })
          .catch((error) => {
            console.error("Registration failure:", error);
          });
    },

    redirectToMain() {
    this.$router.push("/main");
    },
  },
};
</script>

<style scoped>
.cookie-popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(0, 0, 0, 0.7);
  z-index: 1000;
}

.cookie-content {
  position: relative;
  width: 80%;
  max-width: 500px;
  background-color: #fff;
  padding: 40px 20px;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.cookie-content p {
  font-size: 20px;
  margin: 0 0 40px;
}

.reject-button {
  position: absolute;
  bottom: 20px;
  left: 20px;
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.reject-button:hover {
  background-color: #c82333;
}

.accept-button {
  position: absolute;
  bottom: 20px;
  right: 20px;
  background-color: #28a745;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.accept-button:hover {
  background-color: #218838;
}
</style>
