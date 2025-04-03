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
import { BACKEND_URL } from "@/assets/globalConstants"

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
        sessionID: localStorage.getItem("sessionID") || "",
      },
      showCookiePopup: true,
    };
  },
  methods: {
    getTranslation,
    checkUserSession() {
      const sessionID = localStorage.getItem("sessionID");
      if (sessionID) {
        console.log("SessionID found, redirecting to /main...");
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
        const response = await fetch(BACKEND_URL + "/login", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            username: this.form.username,
            password: this.form.password,
          }),
        });

        const data = await response.json();

        if (response.ok) {
          console.log("Login successful. Redirecting to /main...");
          localStorage.setItem("sessionID", data.sessionID);
          this.router.push(`/main?sessionID=${data.sessionID}`);
        } else {
          // Handle different HTTP error codes
          switch (response.status) {
            case 401:
              alert(getTranslation(this.currentLanguage, "INVALID_CREDENTIALS"));
              break;
            case 404:
              alert(getTranslation(this.currentLanguage, "USER_NOT_FOUND"));
              break;
            case 429:
              alert(getTranslation(this.currentLanguage, "TOO_MANY_ATTEMPTS"));
              break;
            case 500:
              alert(getTranslation(this.currentLanguage, "SERVER_ERROR"));
              break;
            default:
              alert(getTranslation(this.currentLanguage, "LOGIN_FAILED"));
          }
        }
      } catch (error) {
        console.error("Login error:", error);
        alert("An error occurred while trying to log in.");
      }
    },


       async register() {
      try {
        const response = await fetch(BACKEND_URL + "/register", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            username: this.form.username,
            password: this.form.password,
          }),
        });
        sessionStorage.setItem("username", this.form.username);

        if (response.ok) {
          alert(getTranslation(this.currentLanguage, "REGISTRATION_SUCCESS"));
          this.router.push("/login");
        } else {
          // Handle different HTTP error codes
          switch (response.status) {
            case 409:
              alert(getTranslation(this.currentLanguage, "USERNAME_TAKEN"));
              break;
            case 400:
              alert(getTranslation(this.currentLanguage, "INVALID_INPUT"));
              break;
            case 500:
              alert(getTranslation(this.currentLanguage, "SERVER_ERROR"));
              break;
            default:
              alert(getTranslation(this.currentLanguage, "REGISTRATION_ERROR"));
          }
        }
      } catch (error) {
        console.error(getTranslation(this.currentLanguage, "REGISTRATION_FAILED"), error);
        alert(getTranslation(this.currentLanguage, "REGISTRATION_ERROR"));
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
