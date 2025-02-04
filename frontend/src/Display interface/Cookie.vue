<template>
  <div class="cookie-popup">
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
      this.$emit('consent-choice', isConsent);
      this.signUp();
    },

  // Setting Cookies
  setConsentCookie(isConsent) {
    const consentValue = isConsent ? "true" : "false";
    const d = new Date();
    d.setTime(d.getTime() + 30 * 24 * 60 * 60 * 1000); // Set a 30-day expiry date
    const expires = "expires=" + d.toUTCString();
    document.cookie = `optionalConsent=${consentValue};${expires};path=/`;
  },

  // Calling the Registration API
  async signUp() {
    try {
      const response = await fetch("http://localhost:8080/signup", {
        method: "GET",
        credentials: "include",
      });

      if (!response.ok) {
        throw new Error(`Non-200 response: ${response.status}`);
      }

      // Read the userID from the cookie and store it in LocalStorage.
      this.storeUserID();

      // Hide Cookie Popups
      document.getElementById("cookiePopUp").style.display = "none";

      // Skip to main screen
      this.redirectToMain();
    } catch (error) {
      console.error("Registration failure:", error);
    }
  },

  // Extracts the userID from the cookie and stores it in LocalStorage.
  storeUserID() {
    const cookies = document.cookie.split("; ").reduce((acc, cookie) => {
      const [key, value] = cookie.split("=");
      acc[key] = value;
      return acc;
    }, {});

    if (cookies.userID) {
      localStorage.setItem("userId", cookies.userID);
    }
  },
},
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
