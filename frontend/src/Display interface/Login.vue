<template>
  <div class="login-container">
    <Cookie v-if="showCookiePopup" @consent-choice="handleConsent" :currentLanguage="currentLanguage"/>

    <div class="card">
      <h2>
        {{
          isLoginMode ?
              getTranslation(currentLanguage, "LOG_IN") :
              getTranslation(currentLanguage, "REGISTER")
        }}
      </h2>


      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="username">
            {{
              getTranslation(currentLanguage, "USERNAME")
            }}
          </label>
          <input v-model="form.username" id="username" type="text" required />
        </div>
        <div class="form-group">
          <label for="password">
            {{
              getTranslation(currentLanguage, "PASSWORD")
            }}
          </label>
          <input v-model="form.password" id="password" type="password" required />
        </div>
        <div v-if="!isLoginMode" class="form-group">
          <label for="confirmPassword">
            {{
              getTranslation(currentLanguage, "CONFIRM_PASSWORD")
            }}
          </label>
          <input v-model="form.confirmPassword" id="confirmPassword" type="password" required />
        </div>
        <button type="submit" :disabled="showCookiePopup">
          {{
            isLoginMode ?
                getTranslation(currentLanguage, "LOG_IN") :
                getTranslation(currentLanguage, "REGISTER")
          }}
        </button>
      </form>
      <p class="toggle-text">
        {{
          isLoginMode ?
              getTranslation(currentLanguage, "DONT_HAVE_AN_ACCOUNT") :
              getTranslation(currentLanguage, "ALREADY_HAVE_AN_ACCOUNT")
        }}
        <span @click="toggleMode">
          {{
            isLoginMode ?
                getTranslation(currentLanguage, "REGISTER") :
                getTranslation(currentLanguage, "LOG_IN")
          }}
        </span>
      </p >
    </div>
  </div>
</template>

<script>
import { useRouter } from "vue-router";
import Cookie from '../Display interface/Cookie.vue';
import {getTranslation} from "../assets/language";

export default {
  components: { Cookie },
  setup() {
    const router = useRouter();
    return { router };
  },
  props: ["currentLanguage"],

  data() {
    return {
      isLoginMode: true,
      form: {
        username: "",
        password: "",
        confirmPassword: "",
      },
      showCookiePopup: true,
    };
  },
  methods: {
    getTranslation,
    checkUserSession() {
      const userID = localStorage.getItem("userId");
      if (userID) {
        console.log("UserID found, redirecting to /main...");
        this.$router.push("/main");
      }
    },
    toggleMode() {
      this.isLoginMode = !this.isLoginMode;
      this.form.password = "";
      this.form.confirmPassword = "";
    },


    async handleConsent(consent) {
      console.log("User consented:", consent);
      this.showCookiePopup = false;
    },

    async handleSubmit() {
      if (this.isLoginMode) {
        this.login();
      } else {
        if (this.form.password !== this.form.confirmPassword) {
          alert(
              getTranslation(this.currentLanguage, "PASSWORDS_DO_NOT_MATCH")
          );
          return;
        }
        this.register();
      }
    },

    async login() {
      try {
        const response = await fetch("http://localhost:8080/login", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            username: this.form.username,
            password: this.form.password,
          }),
        });

        const data = await response.json();
        if (response.ok && data.success) {
          console.log("Login successful. Redirecting to /main...");
          localStorage.setItem("username", this.form.username);
          this.router.push(`/main?username=${this.form.username}`); 
        } else {
          alert(data.message || "Login failed!");
        }
      } catch (error) {
        console.error("Login error:", error);
        alert("An error occurred while trying to log in.");
      }
    },

    async register() {
      try {
        const response = await fetch("http://localhost:8080/register", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            username: this.form.username,
            password: this.form.password,
          }),
        });
        sessionStorage.setItem("username", this.form.username);

        const data = await response.json();
        if (response.ok && data.success) {
          alert("Registration successful! Redirecting to login...");
          this.router.push("/login");
        } else {
          alert(data.message || "Registration failed!");
        }
      } catch (error) {
        console.error("Registration error:", error);
        alert("An error occurred while trying to register.");
      }
    },
  },
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f4f4f4;
}

.card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 300px;
  text-align: center;
}

h2 {
  margin-bottom: 1rem;
}

.form-group {
  margin-bottom: 1rem;
  text-align: left;
}

label {
  display: block;
  font-size: 14px;
  margin-bottom: 0.5rem;
}

input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  width: 100%;
  padding: 0.75rem;
  border: none;
  border-radius: 4px;
  background-color: #5c88da;
  color: white;
  font-size: 16px;
  cursor: pointer;
}

button:hover {
  background-color: #3f70d1;
}

.toggle-text {
  margin-top: 1rem;
  font-size: 14px;
}

.toggle-text span {
  color: #5c88da;
  cursor: pointer;
  font-weight: bold;
}
</style>
