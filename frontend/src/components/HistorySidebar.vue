<template>
  <div class="history-sidebar">
    <button class="add-chat-btn" @click="addChat">Add Chat</button>
    <div class="chat-list">
      <ul>
        <li v-if="isLoading">Loading chats...</li>
        <li v-else-if="chats.length === 0">No chats available. Click "Add Chat" to start a new one!</li>
        <li v-for="chat in chats" :key="chat.id" v-else>
          <button
            @click="selectChat(chat.id)"
            :class="{ active: chat.id === selectedChatID }">
            {{ chat.name }}
          </button>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      chats: [],
      selectedChatID: localStorage.getItem("chatId") || null,
      chatHistory: [],
      aiServerUrl: "http://localhost:8080",
      isLoading: false,
    };
  },
  methods: {
    async addChat() {
      // TODO: somehow revert the state of MainContent
    },

    selectChat(chatID) {
      // TODO: Rewrite, MainContent has to request and render chat history
      this.selectedChatID = chatID;
      localStorage.setItem("chatId", chatID);
      this.$emit("chat-selected", chatID);
      this.loadChatHistory(chatID);
    },

    // TODO: No such backend method (getAllChats), rewrite, fetch IDs (and maybe titles) from localStorage
    async loadChats() {
      this.isLoading = true;
      try {
        const response = await axios.get(`${this.aiServerUrl}/getAllChats`, {
          withCredentials: true,
        });

        if (response.status !== 200) {
          throw new Error(`Unexpected response code: ${response.status}`);
        }

        const chats = response.data;
        this.chats = chats.map((chat, index) => ({
          id: chat.id,
          name: chat.name || `Chat ${index + 1}`,
        }));
      } catch (error) {
        console.error("Error loading chats:", error);
        alert("Failed to load chats. Please check your login status.");
      } finally {
        this.isLoading = false;
      }
    },
  },
  mounted() {
    this.loadChats();
  },
};
</script>

<style scoped>
.history-sidebar {
  width: 250px;
  background-color: #f8f9fa;
  padding: 10px;
  border-right: 1px solid #ddd;
}

.add-chat-btn {
  display: block;
  margin: 10px auto;
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.add-chat-btn:hover {
  background-color: #0056b3;
}

.chat-list ul {
  list-style: none;
  padding: 0;
}

.chat-list li {
  margin: 5px 0;
}

.chat-list button {
  background: none;
  border: none;
  color: #007bff;
  cursor: pointer;
}

.chat-list button.active {
  font-weight: bold;
  color: #0056b3;
}

.chat-list button:hover {
  text-decoration: underline;
}
</style>
