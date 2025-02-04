<template>
  <!-- Sidebar for chat history -->
  <aside class="history-sidebar" :class="{ collapsed: isCollapsed }" :style="asideStyles">
    <!-- Toggle Button (Top Left) -->
    <button class="toggle-btn" @click="toggleSidebar" :title="isCollapsed ? 'Open History' : 'Close History'">
      <i class="icon">💬</i> 
    </button>

    <!-- Sidebar Content (Only shown when expanded) -->
    <div v-if="!isCollapsed" class="history-content">
      <!-- New Conversation Button -->
      <button class="new-conversation-btn" @click="addChat" :style="buttonStyles" title="New Chat">
        <i class="icon">➕</i> New Conversation
      </button>

      <!-- Scrollable Conversation List -->
      <div class="history-list">
        <div v-for="chat in chats" :key="chat.chatID">
          <button class="chat-item" @click="selectChat(chat.chatID)" :style="buttonStyles">
            {{ chat.title }}
          </button>
        </div>
      </div>
    </div>
  </aside>
</template>

<script>
import { getTheme } from "../assets/color.js";

export default {
  props: ["chats", "currentChatID"],
  data() {
    return {
      isCollapsed: false, // Controls whether the sidebar is open or closed
      aiServerUrl: "http://localhost:8080", // Placeholder server URL
      currentTheme: "default", // Tracks the current theme
      themeStyles: {}, // Holds dynamic styles for theming
    };
  },
  methods: {
    // Creates a new chat and resets main content
    addChat() {
      this.$emit("resetMainContent");
    },

    // Toggles the sidebar open/closed
    toggleSidebar() {
      this.isCollapsed = !this.isCollapsed;
    },

    // Selects a conversation from the history
    selectChat(chatID) {
      this.$emit("chatSelected", chatID);
    },

    // Applies the selected theme
    applyTheme(themeName) {
      const theme = getTheme(themeName);
      this.themeStyles = {
        aside: {
          backgroundColor: theme.background,
          color: theme.text,
          border: `1px solid ${theme.border}`,
        },
        button: {
          backgroundColor: theme.button,
          color: theme.text,
        },
        text: {
          color: theme.text,
        },
      };
    },

    // Listens for theme changes and updates styles
    listenForThemeChange() {
      window.addEventListener("themeChange", (event) => {
        this.applyTheme(event.detail.themeName);
      });
    },
  },
  computed: {
    asideStyles() {
      return this.themeStyles.aside;
    },
    buttonStyles() {
      return this.themeStyles.button;
    },
  },
  mounted() {
    this.applyTheme("default");
    this.listenForThemeChange();
  },
};
</script>

<style scoped>
/* Sidebar Layout */
.history-sidebar {
  width: 220px; /* ChatGPT-like width */
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--sidebar-bg, #f9f9f9);
  transition: width 0.3s ease-in-out, box-shadow 0.3s ease-in-out;
  border-right: 1px solid rgba(0, 0, 0, 0.1);
  padding: 10px;
  overflow: hidden;
}

/* Fully Collapsed Sidebar */
.history-sidebar.collapsed {
  width: 0; /* Completely hidden when collapsed */
  padding: 0;
  border: none;
}

/* Toggle Button - Moved to Top Left */
.toggle-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 8px;
  position: absolute;
  top: 10px;
  left: 10px;
  font-size: 20px;
}

.toggle-btn .icon {
  font-size: 24px;
}

/* New Conversation Button (Styled like a chat bubble) */
.new-conversation-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 12px;
  font-weight: bold;
  cursor: pointer;
  border-radius: 8px;
  transition: background-color 0.2s ease-in-out, transform 0.2s;
  margin-bottom: 10px;
  background: rgba(0, 0, 0, 0.05);
  border: none;
}

.new-conversation-btn:hover {
  background-color: rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.new-conversation-btn .icon {
  margin-right: 8px;
  font-size: 18px;
}

/* Chat List */
.history-list {
  flex-grow: 1;
  overflow-y: auto;
  padding-right: 5px;
  scrollbar-width: thin;
  scrollbar-color: rgba(0, 0, 0, 0.2) transparent;
}

/* Chat Item Buttons - Centered and Without Bullet Points */
.chat-item {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  text-align: center; /* Center text */
  transition: background-color 0.2s ease-in-out, transform 0.2s;
  margin: 8px 0; /* Added space above and below */
  list-style-type: none; /* Remove bullets */
}

.chat-item:hover {
  background-color: rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}
</style>
