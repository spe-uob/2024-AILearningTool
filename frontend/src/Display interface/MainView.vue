<template>
  <div class="main-view">
    <div class="left-sidebar-container">
      <HistorySidebar @resetMainContent="this.resetMainContent"/>
      <SettingSidebar @toggleSettings="toggleSettings" />
    </div>
    <MainContent @selectTopic="this.topicSelected = true"
                 :topicSelected="this.topicSelected"
                 :messages="this.messages"
                 @addMessage="(a, b) => this.addMessage(a, b)"
    />
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
      topicSelected: false,
      messages: [],
      isSettingsOpen: false,
    };
  },
  methods: {
    // Used to reset MainContent component (e.g. when "Add chat" button is clicked)
    resetMainContent() {
      this.topicSelected = false;
      this.messages = [];
    },
    // Add message to "message" variable in MainView
    addMessage(senderArg, contentArg) {
      this.messages.push({
        sender: senderArg,
        content: contentArg
      })
    },
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
