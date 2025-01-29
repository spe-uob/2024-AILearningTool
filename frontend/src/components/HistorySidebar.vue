<template>
  <div class="history-sidebar">
    <button class="add-chat-btn" @click="addChat">Add Chat</button>
    <div class="chat-list">
      <ul>
        <li v-for="chat in chats" :key="chat.id">
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
      chats: [], // 聊天会话列表
      selectedChatID: localStorage.getItem("chatId") || null, // 存储当前会话 ID
      chatHistory: [], // 聊天记录
      aiServerUrl: "http://localhost:8080/api", // 服务器 API
    };
  },
  methods: {
    // **创建新聊天**
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

        const newChatID = response.data; // 服务器返回 chatID
        this.chats.push({ id: newChatID, name: `Chat ${this.chats.length + 1}` });

        // 选中新创建的聊天
        this.selectChat(newChatID);
      } catch (error) {
        console.error("Error creating chat:", error);
      }
    },

    // **选择聊天并加载聊天记录**
    selectChat(chatID) {
      this.selectedChatID = chatID;
      localStorage.setItem("chatId", chatID); // 存储当前聊天 ID
      this.$emit("chat-selected", chatID); // 触发事件通知 `MainContent.vue`
      this.loadChatHistory(chatID); // 加载聊天记录
    },

    // **加载聊天记录**
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

        // 解析聊天记录
        this.chatHistory = this.processChatHistory(messageHistory);
      } catch (error) {
        console.error("Error loading chat history:", error);
      }
    },

    // **解析聊天记录**
    processChatHistory(messageHistory) {
      const messages = [];
      const lines = messageHistory.split("\n");

      for (const line of lines) {
        if (line.includes("<|system|>") || line.includes("<|assistant|>")) {
          // 跳过系统消息
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

    // **加载所有聊天会话**
    async loadChats() {
      try {
        const response = await axios.get(`${this.aiServerUrl}/getChatHistory`, {
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
      }
    },
  },
  mounted() {
    this.loadChats(); // 页面加载时获取聊天列表
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
