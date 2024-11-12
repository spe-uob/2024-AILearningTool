<template>
  <div id="app">
    <!-- If user is not logged in, show the login screen -->
    <Login v-if="!isLoggedIn" @login-success="handleLoginSuccess" />

    <!-- If user is logged in, show the main content -->
    <div v-else class="app-container">
      <div class="left-sidebar-container">
        <HistorySidebar />
        <SettingSidebar @toggleSettings="toggleSettings" />
      </div>
      <MainContent />
      <ImportantSidebar :isDisabled="isSettingsOpen" />
    </div>
  </div>
</template>

<script>
import Login from './components/Login.vue';
import HistorySidebar from './components/HistorySidebar.vue';
import MainContent from './components/MainContent.vue';
import ImportantSidebar from './components/ImportantSidebar.vue';
import SettingSidebar from './components/SettingSidebar.vue';

export default {
  data() {
    return {
      isLoggedIn: false,  // Controls login state
      isSettingsOpen: false,  // Controls if settings are open
    };
  },
  methods: {
    handleLoginSuccess() {
      this.isLoggedIn = true;  // Set to true on successful login
    },
    toggleSettings(isOpen) {
      this.isSettingsOpen = isOpen;
    },
  },
  components: {
    Login,
    HistorySidebar,
    MainContent,
    ImportantSidebar,
    SettingSidebar,
  },
};
</script>

<style>
.app-container {
  display: flex;
  height: 100vh;
}

.left-sidebar-container {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100vh;
}
</style>
