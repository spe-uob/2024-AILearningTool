<template>
  <div className="main-view">
    <div className="left-sidebar-container">
      <HistorySidebar
          @resetMainContent="resetMainContent"
          @chatSelected="(id) => loadChat(id)"
          :currentChatID="this.currentChatID"
          :chats="chats"
          :currentLanguage="currentLanguage"
          @updateChats="updateChatList"
      />
      <SettingSidebar @toggleSettings="toggleSettings"/>
    </div>
    <MainContent
        :messages="messages"
        :chats="chats"
        :currentChatID="this.currentChatID"
        @addMessage="(a, b) => addMessage(a, b)"
        @addChat="(a, b) => addChat(a, b)"
        @chatSelected="(id) => loadChat(id)"
        :currentLanguage="currentLanguage"
        @setButtonLock="(status) => this.chatInitButtonsDisabled = status"
    />
  </div>
</template>

<script>
import HistorySidebar from "../components/HistorySidebar.vue";
import MainContent from "../components/MainContent.vue";
import SettingSidebar from "../components/SettingSidebar.vue";

export default {
  name: "MainView",
  data() {
    return {
      messages: [],
      isSettingsOpen: false,
      chats: [], // Stores id-title pair for every chat
      currentChatID: "",
      aiServerUrl: "http://localhost:8080",
    };
  },
  props: ["currentLanguage"],
  methods: {
    updateChatList(newChats) {
        this.chats = newChats;
    },
  
    resetMainContent() {
      this.currentChatID = "";
      this.messages = [];
    },

    loadChat(chatID) {
      this.resetMainContent()
      this.currentChatID = chatID
    },

    addMessage(senderArg, contentArg) {
      this.messages.push({
        sender: senderArg,
        content: contentArg,
      });
    },

    addChat(chatID, title) {
      this.chats.push({
        chatID: chatID,
        title: title
      })
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
