<template>
  <div class="main-view">
    <div class="left-sidebar-container">
      <HistorySidebar
          @resetMainContent="this.resetMainContent"
          @chatSelected="(id) => this.loadChat(id)"
          :currentChatID="this.currentChatID"
          :chats="this.chats"
      />
      <SettingSidebar @toggleSettings="toggleSettings" />
    </div>
    <MainContent
        :messages="this.messages"
        :chats="this.chats"
        :currentChatID="this.currentChatID"
        @addMessage="(a, b) => this.addMessage(a, b)"
        @addChat="(a, b) => this.addChat(a, b)"
        @updateChatID="(id) => this.currentChatID = id"
    />
  </div>
</template>

<script>
import HistorySidebar from '../components/HistorySidebar.vue';
import MainContent from '../components/MainContent.vue';
import SettingSidebar from '../components/SettingSidebar.vue';
export default {
  name: 'MainView',
  data() {
    return {
      messages: [],
      isSettingsOpen: false,
      chats: JSON.parse(localStorage.getItem("chats")) || [], // Stores id-title pair for every chat
      currentChatID: ""
    };
  },
  methods: {
    // Used to reset MainContent component (e.g. when "Add chat" button is clicked)
    resetMainContent() {
      this.currentChatID = "";
      this.messages = [];
    },
    // Load existing chat
    loadChat(id) {
      this.resetMainContent()
      this.currentChatID = id
    },
    // Add message to "message" variable in MainView
    addMessage(senderArg, contentArg) {
      this.messages.push({
        sender: senderArg,
        content: contentArg
      })
    },
    // Add a new chat to chat list (used in HistorySidebar) + save to localStorage
    addChat(chatID, title) {
      this.chats.push({
        chatID: chatID,
        title: title
      })
      localStorage.setItem("chats", JSON.stringify(this.chats))
    },
    toggleSettings(isOpen) {
      this.isSettingsOpen = isOpen;
    },
  },
  components: {
    HistorySidebar,
    MainContent,
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
