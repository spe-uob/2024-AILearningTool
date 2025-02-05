<template>
  <aside>
    <!-- Fixed setting button at bottom-left -->
    <button class="settings-btn" @click="openSettings">⚙️</button>

    <!-- Modal for settings -->
    <div v-if="isSettingsOpen" class="modal-overlay">
      <div class="modal-content">
        <h3>Settings</h3>
        <ul>
          <li>
            <h4>1) Language</h4>
            <div class="language-buttons">
              <button @click="changeLanguage('en')">English</button>
              <button @click="changeLanguage('zh')">Chinese</button>
            </div>
          </li>
          <li>
            <h4>2) High Contrast Mode</h4>
            <button @click="toggleHighContrastMode">
              {{ isHighContrast ? 'Turn Off High Contrast Mode' : 'Turn On High Contrast Mode' }}
            </button>
          </li>
        </ul>
        <div class="action-buttons">
          <button @click="goToCookiePage">Go to Cookie Settings</button>
          <button class="close-btn" @click="closeSettings">Close</button>
        </div>
      </div>
    </div>
  </aside>
</template>

<script>
import { getTheme } from "../assets/color.js";

export default {
  data() {
    return {
      isSettingsOpen: false,
      isHighContrast: false,
    };
  },
  methods: {
    openSettings() {
      this.isSettingsOpen = true;
    },
    closeSettings() {
      this.isSettingsOpen = false;
    },
    changeLanguage(language) {
      alert(`Language changed to ${language === 'en' ? 'English' : 'Chinese'}`);
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
    goToCookiePage() {
      this.$router.push("/");
    },
  },
  mounted() {
    this.applyTheme("default");
  },
};
</script>

<style scoped>
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
  background-color: #f44336;
  color: white;
}

.close-btn:hover {
  background-color: #d32f2f;
}
</style>
