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
            <h4>1) Day/Night Mode</h4>
            <button @click="toggleTheme">Toggle Day/Night</button>
          </li>
          <li>
            <h4>2) Language</h4>
            <div class="language-buttons">
              <button @click="changeLanguage('en')">English</button>
              <button @click="changeLanguage('zh')">Chinese</button>
            </div>
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
export default {
  data() {
    return {
      isSettingsOpen: false,
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
    toggleTheme() {
      alert("Day/Night Mode toggled");
    },
    changeLanguage(language) {
      if (language === "en") {
        alert("Language changed to English");
      } else if (language === "zh") {
        alert("Language changed to Chinese");
      }
    },
    goToCookiePage() {
      // Jump to the /cookie page to view or modify cookie settings
      this.$router.push("/");
    },
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
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 350px;
  max-width: 90%;
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
  padding: 8px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.close-btn {
  background-color: #f44336;
  color: white;
}

.close-btn:hover {
  background-color: #d32f2f;
}

button:hover {
  background-color: #e0e0e0;
}
</style>
