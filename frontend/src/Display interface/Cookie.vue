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
    // 处理用户同意或拒绝
    handleConsent(isConsent) {
      this.setConsentCookie(isConsent);
      this.signUp();
    },

    // 设置 Cookie
    setConsentCookie(isConsent) {
      const consentValue = isConsent ? "true" : "false";
      const d = new Date();
      d.setTime(d.getTime() + 30 * 24 * 60 * 60 * 1000); // 设置 30 天有效期
      const expires = "expires=" + d.toUTCString();
      document.cookie = `optionalConsent=${consentValue};${expires};path=/`;
    },

    // 调用注册 API
    async signUp() {
      try {
        const response = await fetch("http://localhost:8080/api/signup", {
          method: "GET",
          credentials: "include",
        });

        if (!response.ok) {
          throw new Error(`Non-200 response: ${response.status}`);
        }

        // 从 Cookie 中读取 userID 并存储到 LocalStorage
        this.storeUserID();

        // 隐藏 Cookie 弹窗
        document.getElementById("cookiePopUp").style.display = "none";

        // 跳转到主界面
        this.redirectToMain();
      } catch (error) {
        console.error("Registration failure:", error);
        alert("Failed to sign up. Please try again.");
      }
    },

    // 从 Cookie 中提取 userID 并存储到 LocalStorage
    storeUserID() {
      const cookies = document.cookie.split("; ").reduce((acc, cookie) => {
        const [key, value] = cookie.split("=");
        acc[key] = value;
        return acc;
      }, {});

      if (cookies.userID) {
        localStorage.setItem("userId", cookies.userID);
      } else {
        console.error("userID cookie not found after signup.");
      }
    },

    // 跳转到主界面
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

button {
  font-size: 16px;
  padding: 12px 24px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  color: white;
}

button:focus {
  outline: 2px solid #0056b3;
}

.reject-button {
  position: absolute;
  bottom: 20px;
  left: 20px;
  background-color: #dc3545;
}

.reject-button:hover {
  background-color: #c82333;
}

.accept-button {
  position: absolute;
  bottom: 20px;
  right: 20px;
  background-color: #28a745;
}

.accept-button:hover {
  background-color: #218838;
}
</style>
