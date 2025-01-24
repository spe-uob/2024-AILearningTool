<template>
  <div class="history-sidebar">
    <button class="add-chat-btn" @click="addChat">Add Chat</button>
    <div class="chat-list">
      <ul>
        <li v-for="chat in chats" :key="chat.id">
          <button @click="selectChat(chat.id)">{{ chat.name }}</button>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      chats: [], // 会话列表
      selectedChatID: null, // 当前选中的会话 ID
    };
  },
  methods: {
    // 添加新聊天
    addChat() {
      fetch("http://localhost:8080/createChat", {
        method: "GET",
        credentials: "include",
      })
          .then(async (response) => {
            if (!response.ok) {
              throw new Error("Non-200 backend API response");
            }
            const newChatID = await response.text();
            // 添加新会话到会话列表
            const newChat = {
              id: newChatID,
              name: `Chat ${this.chats.length + 1}`,
            };
            this.chats.push(newChat);
          })
          .catch((error) => {
            console.error("Error creating chat:", error);
          });
    },
    // 选择聊天
    selectChat(chatID) {
      this.selectedChatID = chatID;
      localStorage.setItem("chatID", chatID);
      this.$emit("chat-selected", chatID); // 通知父组件更新内容
    },
    // 加载历史会话
    loadChatHistory() {
      fetch("http://localhost:8080/getChatHistory", {
        method: "GET",
        credentials: "include",
      })
          .then(async (response) => {
            if (!response.ok) {
              throw new Error("Failed to load chat history");
            }
            const chatHistory = await response.json();
            this.chats = chatHistory.map((chat, index) => ({
              id: chat.id,
              name: `Chat ${index + 1}`,
            }));
          })
          .catch((error) => {
            console.error("Error loading chat history:", error);
          });
    },
  },
  mounted() {
    this.loadChatHistory();
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

.chat-list button:hover {
  text-decoration: underline;
}
</style>
