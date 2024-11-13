<template>
  <div class="main-view">
    <div class="left-sidebar-container">
      <HistorySidebar />
      <SettingSidebar @toggleSettings="toggleSettings" />
    </div>
    <MainContent />
    <ImportantSidebar :isDisabled="isSettingsOpen" />
  </div>
</template>

<script>
import HistorySidebar from '../components/HistorySidebar.vue';
import MainContent from '../components/MainContent.vue';
import ImportantSidebar from '../components/ImportantSidebar.vue';
import SettingSidebar from '../components/SettingSidebar.vue';
export default {
  name: 'MainView',
  data() {
    return {
      isSettingsOpen: false, // Control whether important parts are disabled
    };
  },
  methods: {
    toggleSettings(isOpen) {
      this.isSettingsOpen = isOpen;
    },
  },
  components: {
    HistorySidebar,
    MainContent,
    ImportantSidebar,
    SettingSidebar,
  },
};
</script>

<style scoped>
.main-view {
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
