<template>
  <div className="main-view">
    <div className="left-sidebar-container">
      <HistorySidebar
          @resetMainContent="resetMainContent"
          @chatSelected="(id) => loadChat(id)"
          :currentChatID="currentChatID"
          :chats="chats"
          :currentLanguage="currentLanguage"
          @updateChats="updateChatList"
      />
      <SettingSidebar @toggleSettings="toggleSettings"/>
    </div>
    <MainContent
        :messages="messages"
        :chats="chats"
        :currentChatID="currentChatID"
        @addMessage="(a, b) => addMessage(a, b)"
        @addChat="(a, b) => addChat(a, b)"
        @updateChatID="(id) => currentChatID = id"
        :currentLanguage="currentLanguage"
        @setButtonLock="(status) => this.chatInitButtonsDisabled = status"
    />
  </div>
</template>

<script>
import axios from "axios";
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



    processChatHistory(messageHistory) {
      let messages = [];
      let lines = messageHistory.split("\n");

      for (let i = 0; i < lines.length; i++) {
        let sender = "assistant";
        if (lines[i].startsWith("<|user|>")) sender = "user";
        else if (lines[i].startsWith("<|assistant|>")) sender = "assistant";
        else continue;

        let message = lines[i + 1] || "";
        if (message.trim()) {
          messages.push({sender, content: message});
        }
      }
      return messages;
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
