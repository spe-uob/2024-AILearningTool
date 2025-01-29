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
      const initialMessage = "Hello, this is a new chat!";

      try {
        const response = await axios.get(`${this.aiServerUrl}/createChat`, {
          params: { initialMessage },
          withCredentials: true,
        });

        if (response.status !== 200) {
          throw new Error(`Unexpected response code: ${response.status}`);
        }

        const newChatID = response.data;
        this.chats.push({ id: newChatID, name: `Chat ${this.chats.length + 1}` });

        // Select new chat
        this.selectChat(newChatID);
      } catch (error) {
        console.error("Error creating chat:", error);
        alert("Failed to create a new chat. Please try again.");
      }
    },

    selectChat(chatID) {
      this.selectedChatID = chatID;
      localStorage.setItem("chatId", chatID);
      this.$emit("chat-selected", chatID);
      this.loadChatHistory(chatID);
    },

    async loadChatHistory(chatID) {
      try {
        const response = await axios.get(`${this.aiServerUrl}/getChatHistory`, {
          params: { chatID },
          withCredentials: true,
        });

        if (response.status !== 200) {
          throw new Error(`Unexpected response code: ${response.status}`);
        }

        const messageHistory = response.data;
        this.chatHistory = this.processChatHistory(messageHistory);
      } catch (error) {
        console.error("Error loading chat history:", error);
        alert("Failed to load chat history. Please try again.");
      }
    },

    processChatHistory(messageHistory) {
      const messages = [];
      const lines = messageHistory.split("\n");

      for (const line of lines) {
        if (line.includes("<|system|>") || line.includes("<|assistant|>")) {
          continue;
        }

        if (line.includes("<|user|>")) {
          messages.push({ sender: "You", content: line.replace("<|user|>", "").trim() });
        } else {
          messages.push({ sender: "AI", content: line.trim() });
        }
      }

      return messages;
    },

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
