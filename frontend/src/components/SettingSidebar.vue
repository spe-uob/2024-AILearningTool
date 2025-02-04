<template>
  <aside>
    <!-- Setting button at the bottom -->
    <button @click="openSettings">⚙️ Setting</button>

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
            <!-- Button to toggle high contrast mode -->
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
      isHighContrast: false, // Tracks the toggle state for high contrast mode
    };
  },
  methods: {
    openSettings() {
      this.isSettingsOpen = true;
      this.$emit("toggleSettings", true); // Notify parent that settings are open
    },
    closeSettings() {
      this.isSettingsOpen = false;
      this.$emit("toggleSettings", false); // Notify parent that settings are closed
    },
    changeLanguage(language) {
      if (language === "en") {
        alert("Language changed to English");
      } else if (language === "zh") {
        alert("Language changed to Chinese");
      }
    },
    toggleHighContrastMode() {
      this.isHighContrast = !this.isHighContrast; // Toggle high contrast mode on/off
      const themeName = this.isHighContrast ? "high_contrast" : "default";
      this.applyTheme(themeName);
      this.$emit("highContrastToggled", this.isHighContrast); // Notify parent
      const event = new CustomEvent("themeChange", { detail: { themeName } });
      window.dispatchEvent(event);
    },
    applyTheme(themeName) {
      const theme = getTheme(themeName);
      Object.keys(theme).forEach((key) => {
        document.documentElement.style.setProperty(`--${key}-color`, theme[key]);
      });
    },
    goToCookiePage() {
      this.$router.push("/"); // Redirect to the /cookie page
    },
  },
  mounted() {
    // Apply default theme on mount
    this.applyTheme("default");
  },
};
</script>

<style scoped>
/* Sidebar settings button */
aside {
  position: absolute;
  bottom: 10px;
  left: 10px;
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
  padding: 20px;
  border-radius: 12px;
  width: 300px;
  max-width: 90%;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  margin-bottom: 20px;
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
  transition: background-color 0.3s ease-in-out, transform 0.2s;
}

button:hover {
  background-color: var(--primary-color);
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.18);
}

.close-btn {
  background-color: #f44336;
  color: white;
}

.close-btn:hover {
  background-color: #d32f2f;
}

button:focus {
  outline: none;
}

button:active {
  transform: scale(0.96);
}

/* Use CSS variables for theming */
:root {
  --primary-color: #000000;
  --secondary-color: #ffffff;
  --accent-color: #b0b0b0;
  --background-color: #f4f4f4;
  --text-color: #2e2e2e;
  --border-color: #d3d3d3;
  --button-color: #4caf50;
  --error-color: #e74c3c;
  --success-color: #27ae60;
}

body {
  color: var(--text-color);
  background-color: var(--background-color);
}
</style>
