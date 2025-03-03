<template>
  <div className="main-view">
    <div className="left-sidebar-container">
      <HistorySidebar
          @resetMainContent="resetMainContent"
          @chatSelected="(id) => loadChat(id)"
          :currentChatID="currentChatID"
          :chats="chats"
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

    async loadChat(chatID) {
      if (!chatID) return;

      this.resetMainContent();
      this.currentChatID = chatID;

      try {
        const response = await axios.get(`${this.aiServerUrl}/getChatHistory`, {
          params: {
            username: sessionStorage.getItem("username"),
            chatID: chatID
          },
        });

        if (response.status === 200) {
          this.messages = this.processChatHistory(response.data.history);
        } else {
          console.error("Failed to fetch chat history.");
        }
      } catch (error) {
        console.error("Error fetching chat history:", error);
      }
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

    async addChat() {
      try {
        const response = await axios.post(`${this.aiServerUrl}/createChat`, {
          username: sessionStorage.getItem("username"),
          initialMessage: "Hello, how can I help?",
        });

        if (response.status === 200) {
          const newChat = {
            chatID: response.data.chatID,
            title: "New Chat",
          };

          this.chats.push(newChat);
          localStorage.setItem("chats", JSON.stringify(this.chats));

          this.loadChat(newChat.chatID);
        } else {
          console.error("Failed to create chat.");
        }
      } catch (error) {
        console.error("Error creating chat:", error);
      }
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
