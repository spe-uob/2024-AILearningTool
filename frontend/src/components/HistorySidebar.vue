<template>
  <aside :class="{'collapsed': isCollapsed}">
    <!-- Toggle Button for Collapsing/Expanding -->
    <button @click="toggleSidebar">History</button>

    <!-- History content, only visible when not collapsed -->
    <div v-if="!isCollapsed" class="history-content">
      <h3>Conversation History</h3>
      <ul>
        <!-- 动态生成聊天记录按钮 -->
        <li v-for="conversation in conversations" :key="conversation.chatId">
          <button @click="loadConversation(conversation.chatId)">
            {{ conversation.chatId }}
          </button>
        </li>
      </ul>

      <!-- Button for adding a new conversation -->
      <button class="new-conversation-btn" @click="createNewConversation">➕ New Conversation</button>
    </div>
  </aside>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      isCollapsed: false, // Controls the collapse state of the sidebar
      conversations: [], // List of conversations (chatId)
    };
  },
  methods: {
    toggleSidebar() {
      this.isCollapsed = !this.isCollapsed;
    },

    // 创建新对话并动态添加到会话列表
    async createNewConversation() {
      try {
        const response = await axios.get('https://ailearningtool.ddns.net:8080/createChat', {
          params: {
            userId: localStorage.getItem('userId'), // 从 LocalStorage 获取 userId
            initialMessage: "", // 空的初始内容
          },
        });

        const chatId = response.data.chatId; // 假设返回字段为 chatId
        console.log('Created chat with ID:', chatId);

        // 动态添加到会话列表
        this.conversations.push({ chatId });
      } catch (error) {
        console.error('Failed to create a new conversation:', error);
        alert('Failed to create a new conversation. Please try again.');
      }
    },

    // 加载指定会话内容
    async loadConversation(chatId) {
      try {
        const response = await axios.get('https://ailearningtool.ddns.net:8080/getChatHistory', {
          params: {
            userId: localStorage.getItem('userId'), // 从 LocalStorage 获取 userId
            chatId, // 当前选择的 chatId
          },
        });

        const chatHistory = response.data.history; // 假设返回字段为 history
        console.log('Chat History:', chatHistory);

        // 将聊天内容传递到父组件（需要父组件监听此事件）
        this.$emit('loadConversation', chatHistory);
      } catch (error) {
        console.error('Failed to load conversation:', error);
        alert('Failed to load the conversation. Please try again.');
      }
    },
  },
};
</script>

<style scoped>
aside {
  width: 250px;
  background-color: #f0f0f0;
  padding: 10px;
  transition: width 0.3s ease;
  position: relative;
}

.collapsed {
  width: 50px;
}

button {
  margin: 5px 0;
}

.history-content {
  margin-top: 10px;
}

.new-conversation-btn {
  margin-top: 10px;
}
</style>
