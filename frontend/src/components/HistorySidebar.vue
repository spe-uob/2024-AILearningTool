<template>
  <aside class="history-sidebar" :class="{ collapsed: isCollapsed }" :style="asideStyles">
    <!-- Toggle Button -->
    <button class="toggle-btn chat-item" @click="toggleSidebar" :style="buttonStyles" :title="isCollapsed ? 'Open History' : 'Close History'">
      <i class="icon">💬</i> <span v-if="!isCollapsed">History</span>
    </button>

    <!-- Sidebar Content -->
    <div v-if="!isCollapsed" class="history-container">
      <!-- New Chat Button -->
      <button class="chat-item" @click="addChat" :style="buttonStyles" title="New Chat">
        <i class="icon">➕</i> WatsonX AI
      </button>

      <!-- Chat History List -->
      <div class="history-list-wrapper">
        <ul>
          <li v-for="chat in chats" :key="chat.chatID">
            <button class="chat-item" @click="selectChat(chat.chatID)" :style="buttonStyles">
              {{ chat.title }}
            </button>
          </li>
        </ul>
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
      isCollapsed: false,
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
.history-sidebar {
  width: 240px;
  height: 90vh;
  display: flex;
  border-radius: 12px;
  flex-direction: column;
  transition: width 0.3s ease-in-out;
  border-right: 2px solid var(--border-color);
  padding: 12px;
  background-color: var(--background-color);
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.history-sidebar.collapsed {
  width: 60px;
}

.toggle-btn {
  width: 100%;
  padding: 14px;
  cursor: pointer;
  text-align: center;
  font-weight: bold;
  transition: background-color 0.3s ease-in-out, transform 0.2s;
  margin: 10px 0;
  background: var(--button-color);
  color: var(--text-color);
  border: none !important;
  border-radius: 10px;
}

.toggle-btn:hover {
  background-color: var(--primary-color);
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.18);
}

.history-container {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  padding-top: 12px;
  overflow: hidden;
  max-height: calc(100vh - 150px);
}

.history-list-wrapper {
  flex-grow: 1;
  overflow-y: auto;
  padding: 10px;
  border-radius: 12px;
  background-color: var(--background-color);
  box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease-in-out;
}

.history-list-wrapper::-webkit-scrollbar {
  width: 6px;
}

.history-list-wrapper::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.chat-item {
  width: 100%;
  padding: 14px;
  cursor: pointer;
  text-align: left;
  font-weight: bold;
  transition: background-color 0.3s ease-in-out, transform 0.2s;
  margin: 10px 0;
  background: var(--button-color);
  color: var(--text-color);
  border: none !important;
  border-radius: 10px;
}

.chat-item:hover {
  background-color: var(--primary-color);
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.18);
}
</style>
