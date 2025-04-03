<template>
  <aside>
    <!-- Fixed setting button at bottom-left -->
    <button class="settings-btn" @click="openSettings">⚙️</button>

    <!-- Modal for settings -->
    <div v-if="isSettingsOpen" class="modal-overlay">
      <div class="modal-content">
        <h3> {{ getTranslation(currentLanguage, "SETTINGS") }}</h3>
        <ul>
          <li>
            <h4>1) {{getTranslation(currentLanguage, "LANGUAGE")}}</h4>
            <div class="language-buttons">
              <button @click="changeLanguage('en')">English</button>
              <button @click="changeLanguage('zh')">简体中文</button>
              <button @click="changeLanguage('ru')">Русский</button>
            </div>
          </li>
          <li>
            <h4>2) {{ getTranslation(currentLanguage, "HIGH_CONTRAST_MODE")}}</h4>
            <button @click="toggleHighContrastMode">
              {{ this.isHighContrast ? getTranslation(currentLanguage, "TURN_OFF") : getTranslation(currentLanguage, "TURN_ON") }}
            </button>
          </li>
        </ul>
        <div class="action-buttons">
          <button @click="Logout">{{ getTranslation(currentLanguage, "LOG_OUT") }}</button>
          <button id="delete-account-btn" @click="deleteAccount">{{ getTranslation(currentLanguage, "DELETE_ACCOUNT") }}</button>
        </div>
        <div id="close-btn-container">
          <button class="close-btn" @click="closeSettings">{{ getTranslation(currentLanguage, "CLOSE")}}</button>
        </div>
      </div>
    </div>
  </aside>
</template>

<script>
import { getTheme } from "@/assets/color";
import { getTranslation } from "@/assets/language";

export default {
  data() {
    return {
      isSettingsOpen: false,
      isHighContrast: false,
    };
  },
  props: ["currentLanguage"],
  methods: {
    getTranslation,
    openSettings() {
      this.isSettingsOpen = true;
    },
    closeSettings() {
      this.isSettingsOpen = false;
    },
    changeLanguage(langCode) {
      this.$emit("updateLanguage", langCode);
      console.log("lang changed to " + langCode);
    },
    toggleHighContrastMode() {
      this.isHighContrast = !this.isHighContrast;
      const themeName = this.isHighContrast ? "high_contrast" : "default";
      this.applyTheme(themeName);
    },
    applyTheme(themeName) {
      const theme = getTheme(themeName);
      Object.keys(theme).forEach((key) => {
        document.documentElement.style.setProperty(`--${key}-color`, theme[key]);
      });
    },

    deleteAccount() {
      // If user changes their mind, don't send the request, otherwise proceed with deleting.
      if (!confirm(getTranslation(this.currentLanguage, "DELETE_ACCOUNT_CONFIRMATION"))) {
        return
      }
      fetch(
          "http://localhost:8080/revokeConsent",
          {
            method: "DELETE",
            credentials: "include",
          }
      ).then(async (response) => {
        if (response.ok) {
          alert("Account has been deleted")
          this.Logout()
        } else {
          alert("Unable to delete the account")
        }
      });
    },

    Logout() {
      localStorage.removeItem("chats");
      localStorage.removeItem("token");
      localStorage.removeItem("sessionID");

      document.cookie.split(";").forEach((cookie) => {
        const name = cookie.split("=")[0].trim();
        document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/`;
      });
      this.$router.push("/"); // Redirect to the /cookie page

    },
  },
  mounted() {
    this.applyTheme("default");
  },
};
</script>

<style>
/* Settings button */
.settings-btn {
  position: fixed;
  bottom: 20px;
  left: 20px;
  background-color: var(--button-color);
  border: none;
  font-size: 24px;
  cursor: pointer;
  padding: 12px;
  border-radius: 50%;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s, transform 0.2s;

  /* Centering the emoji */
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px; 
  height: 50px;
  line-height: 1;
}

.settings-btn:hover {
  background-color: var(--primary-color);
  transform: translateY(-2px);
}

/* Modal styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background-color: var(--background-color);
  padding: 25px;
  border-radius: 12px;
  width: 350px;
  max-width: 90%;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  margin-bottom: 15px;
}

.language-buttons {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

#close-btn-container {
  display: flex;
  justify-content: center;
  margin-top: 75px;
}

button {
  padding: 10px 16px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  background-color: var(--button-color);
  color: var(--text-color);
  font-weight: bold;
  transition: background-color 0.3s, transform 0.2s;
}

button:hover {
  background-color: var(--primary-color);
  transform: translateY(-2px);
}

.close-btn {
  background-color: #b5b5b5;
}

.close-btn:hover {
  background-color: #a0a0a0;
}

#delete-account-btn {
  background-color: #660000;
  font-size: 0.85em;
  font-weight: bolder;
  color: white;
}

#delete-account-btn:hover {
  background-color: #500000;
  color: white;
}
</style>
