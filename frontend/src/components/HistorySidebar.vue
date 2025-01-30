<template>
  <div class="history-sidebar">
    <button class="add-chat-btn" @click="addChat">Add Chat</button>
    <div class="chat-list">
      <ul>
        <li v-if="this.chats.length === 0">No chats available. Click "Add Chat" to start a new one!</li>
        <li v-else v-for="chat in this.chats" :key="chat.chatID">
          <button
            @click="selectChat(chat.chatID)"
            :class="{ active: chat.chatID === this.currentChatID }">
            {{ chat.title }}
          </button>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>

export default {
  data() {
    return {
      aiServerUrl: "http://localhost:8080",
    };
  },
  props: ["chats", "currentChatID"],
  methods: {
    // Reverts the state of MainContent to initial state.
    async addChat() {
      this.$emit("resetMainContent")
    },

    // Sends a chat selection event, asks other components to render the required chat
    selectChat(chatID) {
      this.$emit("chatSelected", chatID);
    },
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
