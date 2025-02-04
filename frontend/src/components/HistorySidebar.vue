<template>
  <aside class="history-sidebar" :class="{ collapsed: isCollapsed }" :style="asideStyles">
    <!-- Toggle Button -->
    <button class="toggle-btn" @click="toggleSidebar">
      <span v-if="isCollapsed">📜</span>
      <span v-else>History</span>
    </button>

    <!-- History content, only visible when not collapsed -->
    <div v-if="!isCollapsed" class="history-content">
      <h3 class="sidebar-title" :style="textStyles">Conversation History</h3>

      <!-- Scrollable conversation list -->
      <div class="history-list">
        <ul>
          <li v-for="chat in chats" :key="chat.chatID">
            <button class="chat-item" @click="selectChat(chat.chatID)" :style="buttonStyles">
              {{ chat.title }}
            </button>
          </li>
        </ul>
      </div>

      <!-- New Conversation Button -->
      <button class="new-conversation-btn" @click="addChat" :style="buttonStyles">
        ➕ New Conversation
      </button>
    </div>
  </aside>
</template>

<script>
import { getTheme } from "../assets/color.js";

export default {
  props: ["chats", "currentChatID"],
  data() {
    return {
      isCollapsed: false,
      aiServerUrl: "http://localhost:8080",
      currentTheme: "default",
      themeStyles: {},
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
        text: {
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
    textStyles() {
      return this.themeStyles.text;
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
  width: 280px;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--sidebar-bg, #f9f9f9);
  transition: width 0.3s ease-in-out, box-shadow 0.3s ease-in-out;
  border-right: 1px solid rgba(0, 0, 0, 0.1);
  padding: 10px;
  overflow: hidden;
}

/* Collapsed Sidebar */
.history-sidebar.collapsed {
  width: 60px;
}

/* Toggle Button */
.toggle-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  font-weight: bold;
  font-size: 16px;
  padding: 8px;
  width: 100%;
  text-align: left;
  transition: opacity 0.3s ease-in-out;
}

.collapsed .toggle-btn {
  text-align: center;
}

/* Sidebar Title */
.sidebar-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
  text-align: center;
}

/* Chat List */
.history-list {
  flex-grow: 1;
  overflow-y: auto;
  max-height: 60vh;
  padding-right: 5px;
  border-radius: 8px;
  scrollbar-width: thin;
  scrollbar-color: rgba(0, 0, 0, 0.2) transparent;
}

/* Chat Item Buttons */
.chat-item {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  text-align: left;
  transition: background-color 0.2s ease-in-out, transform 0.2s;
}

.chat-item:hover {
  background-color: rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

/* New Conversation Button */
.new-conversation-btn {
  width: 100%;
  padding: 12px;
  font-weight: bold;
  cursor: pointer;
  border-radius: 8px;
  transition: background-color 0.2s ease-in-out;
}

.new-conversation-btn:hover {
  background-color: rgba(0, 0, 0, 0.1);
}
</style>
