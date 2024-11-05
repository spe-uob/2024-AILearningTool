<template>
  <aside :class="{'collapsed': isCollapsed}">
    <!-- Toggle Button for Collapsing/Expanding -->
    <button @click="toggleSidebar">History</button>

    <!-- History content, only visible when not collapsed -->
    <div v-if="!isCollapsed" class="history-content">
      <h3>Conversation History</h3>
      <ul>
        <li v-for="(conversation, index) in conversations" :key="index">{{ conversation }}</li>
      </ul>

      <!-- Button for adding a new conversation -->
      <button class="new-conversation-btn" @click="addConversation">➕ New Conversation</button>
    </div>
  </aside>
</template>

<script>
export default {
  data() {
    return {
      isCollapsed: false,  // Controls the collapse state of the sidebar
      conversations: ['Conversation 1'],  // Example list of conversations
    };
  },
  methods: {
    toggleSidebar() {
      this.isCollapsed = !this.isCollapsed;
    },
    addConversation() {
      const newConversation = `Conversation ${this.conversations.length + 1}`;
      this.conversations.push(newConversation);
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
