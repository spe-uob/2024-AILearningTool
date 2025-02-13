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
      </p>
    </div>
  </div>
</template>

<script>
import Cookie from '../Display interface/Cookie.vue';
import {getTranslation} from "../assets/language";

export default {
  components: { Cookie },
  props: ["currentLanguage"],
  data() {
    return {
      isLoginMode: true,
      form: {
        username: '',
        password: '',
        confirmPassword: '',
      },
      showCookiePopup: true,
    };
  },
  mounted() {
    this.checkUserSession();
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
      this.form.password = '';
      this.form.confirmPassword = '';
    },

    async handleSubmit() {
      if (this.showCookiePopup) {
        alert(
            getTranslation(this.currentLanguage, "PLEASE_ACCEPT_COOKIES")
        );
        return;
      }

      if (this.isLoginMode) {
        this.login();
        this.signUp();
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

    async signUp() {
      try {
        const cookies = document.cookie.split("; ").reduce((acc, cookie) => {
          const [key, value] = cookie.split("=");
          acc[key] = value;
          return acc;
        }, {});

        if (!cookies.userID) {
          console.log("No userID found in cookies. Signing up...");

          const response = await fetch("http://localhost:8080/signup", {
            method: "GET",
            credentials: "include",
          });

          if (!response.ok) {
            throw new Error(`Signup failed: ${response.status}`);
          }

          console.log("Signup successful! Checking cookies again...");
          this.storeUserID();
        } else {
          console.log("User already signed up, skipping signup.");
        }
      } catch (error) {
        console.error("Signup error:", error);
      }
    },

    storeUserID() {
      const cookies = document.cookie.split("; ").reduce((acc, cookie) => {
        const [key, value] = cookie.split("=");
        acc[key] = value;
        return acc;
      }, {});

      if (cookies.userID) {
        localStorage.setItem("userId", cookies.userID);
        console.log("UserID stored in localStorage:", cookies.userID);
        this.$router.push("/main");
      }
    },

    login() {
      fetch('/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username: this.form.username,
          password: this.form.password,
        }),
      })
          .then((res) => res.json())
          .then((data) => {
            if (data.success) {
              localStorage.setItem('token', data.token);
              this.$router.push('/main');
            } else {
              alert(
                  getTranslation(this.currentLanguage, "LOGIN_FAILED")
              );
            }
          })
          .catch((err) => {
            console.error('Login error:', err);
            alert(
                getTranslation(this.currentLanguage, "LOGIN_FAILED")
            );
          });
    },

    register() {
      fetch('/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username: this.form.username,
          password: this.form.password,
        }),
      })
          .then((res) => res.json())
          .then((data) => {
            if (data.success) {
              alert(
                  getTranslation(this.currentLanguage, "REGISTRATION_SUCCESS")
              );
              this.toggleMode();
            } else {
              alert(
                  getTranslation(this.currentLanguage, "REGISTRATION_FAILED")
              );
            }
          })
          .catch((err) => {
            console.error('Registration error:', err);
            alert(
                getTranslation(this.currentLanguage, "REGISTRATION_FAILED")
            );
          });
    },

    handleConsent(consent) {
      this.showCookiePopup = false;
    },
  },
};
</script>

<style scoped>
/* CSS code unchanged */
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
  background-color: #5C88DA;
  color: white;
  font-size: 16px;
  cursor: pointer;
}

button:hover {
  background-color: #5C88DA;
}

.toggle-text {
  margin-top: 1rem;
  font-size: 14px;
}

.toggle-text span {
  color: #5C88DA;
  cursor: pointer;
  font-weight: bold;
}
</style>
