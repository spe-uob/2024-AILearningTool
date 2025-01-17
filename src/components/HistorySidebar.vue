<template>
  <aside :class="{'collapsed': isCollapsed}">
    <!-- Toggle Button for Collapsing/Expanding -->
    <button @click="toggleSidebar">History</button>

    <!-- History content, only visible when not collapsed -->
    <div v-if="!isCollapsed" class="history-content">
      <h3>Conversation History</h3>
      <ul>
        <!-- Dynamically generated chat log button -->
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

    // Create new conversations and dynamically add them to the session list
    async createNewConversation() {
      try {
        const response = await axios.get('https://ailearningtool.ddns.net:8080/createChat', {
          params: {
            userId: localStorage.getItem('userId'), // Get userId from LocalStorage
            initialMessage: "", // Empty initial content
          },
        });

        const chatId = response.data.chatId; // Assuming the return field is chatId
        console.log('Created chat with ID:', chatId);

        // Dynamically added to the session list
        this.conversations.push({ chatId });
      } catch (error) {
        console.error('Failed to create a new conversation:', error);
        alert('Failed to create a new conversation. Please try again.');
      }
    },

    // Load the contents of the specified session
    async loadConversation(chatId) {
      try {
        const response = await axios.get('https://ailearningtool.ddns.net:8080/getChatHistory', {
          params: {
            userId: localStorage.getItem('userId'), // Get userId from LocalStorage
            chatId, // The currently selected chatId
          },
        });

        const chatHistory = response.data.history; // Assuming the return field is history
        console.log('Chat History:', chatHistory);

        // Pass the chat content to the parent component (requires the parent component to listen to this event)
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
