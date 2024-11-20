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
            <button @click="changeLanguage">English</button>
            <button @click="changeLanguage">Chinese</button>
          </li>
          <li>
            <h4>3) Cookie</h4>
            <button @click="deleteCookies">Delete All User Information</button>
          </li>
        </ul>
        <button @click="closeSettings" class="close-btn">Close</button>
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
      this.$emit('toggleSettings', true); // Notify parent that settings are open
    },
    closeSettings() {
      this.isSettingsOpen = false;
      this.$emit('toggleSettings', false); // Notify parent that settings are closed
    },
    toggleTheme() {
      alert('Day/Night Mode toggled');
    },
    changeLanguage() {
      alert('Language change triggered');
    },
    deleteCookies() {
      alert('All user information deleted');
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
  width: 300px;
  max-width: 90%;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  margin-bottom: 15px;
}

.close-btn {
  margin-top: 20px;
  padding: 10px;
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
</style>
