<template>
  <aside class="history-sidebar" :class="{ collapsed: isCollapsed }" :style="asideStyles">
    <!-- Toggle Button (Always in Top Left) -->
    <button class="toggle-btn" @click="toggleSidebar" :title="isCollapsed ? 'Open History' : 'Close History'">
      ☰
    </button>

    <!-- Sidebar Content (Only visible when expanded) -->
    <div v-if="!isCollapsed" class="history-container">
      <!-- New Chat Button -->
      <button class="chat-item" @click="addChat" :style="buttonStyles" title="New Chat">
        ➕ New Conversation
      </button>

      <!-- Chat History List -->
      <div class="history-list-wrapper">
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
      isCollapsed: false, // Controls sidebar visibility
      aiServerUrl: "http://localhost:8080",
      currentTheme: "default", // Tracks the current theme
      themeStyles: {}, // Stores dynamic styles
    };
  },
  methods: {
    addChat() {
      this.$emit("resetMainContent");
    },
    toggleSidebar() {
      this.isCollapsed = !this.isCollapsed;
    },
    selectChat(chatID) {
      this.$emit("chatSelected", chatID);
    },
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
      };
    },
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
  width: 240px;
  height: 100vh;
  display: flex;
  flex-direction: column;
  border-right: 2px solid var(--border-color);
  padding: 12px;
  background-color: var(--background-color);
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  transition: width 0.3s ease-in-out;
  overflow: hidden;
  position: relative;
}

/* Fully Collapsed Sidebar */
.history-sidebar.collapsed {
  width: 60px; 
  padding: 0;
  border-right: none;
}

/* Toggle Button (☰ in Top Left) */
.toggle-btn {
  position: absolute;
  top: 10px;
  left: 10px;
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 22px;
  font-weight: bold;
  padding: 8px;
  transition: opacity 0.3s ease-in-out;
  color: black;
}

/* New Chat Button (Lowered) */
.new-chat-btn {
  margin-top: 20px; /* Push it down slightly */
}
  
/* Sidebar Content */
.history-container {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  padding-top: 12px;
  max-height: calc(100vh - 150px);
}

/* Chat History List - No Bullet Points */
.history-list-wrapper {
  flex-grow: 1;
  overflow-y: auto;
  padding: 10px;
  border-radius: 12px;
  background-color: var(--background-color);
  box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.08);
}

/* Centered Chat Bubbles */
.chat-item {
  width: 100%;
  padding: 14px;
  cursor: pointer;
  text-align: center;
  font-weight: bold;
  transition: background-color 0.3s ease-in-out, transform 0.2s;
  margin: 10px 0;
  background: var(--button-color);
  color: var(--text-color);
  border: none;
  border-radius: 10px;
}

.chat-item:hover {
  background-color: var(--primary-color);
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.18);
}

/* Hide bullet points */
.history-list-wrapper ul {
  list-style: none;
  padding: 0;
}
</style>
