<template>
  <aside class="history-sidebar" :class="{ collapsed: isCollapsed }" :style="asideStyles">
    <!-- Toggle Button (Always in Top Left) -->
    <button class="toggle-btn" @click="toggleSidebar" :title="isCollapsed ? 'Open History' : 'Close History'">
      ☰
    </button>

    <!-- Sidebar Content (Only visible when expanded) -->
    <div v-if="!isCollapsed" class="history-container">
      <!-- New Chat Button -->
      <button class="chat-item selectable-chat" @click="addChat" :style="newChatButtonStyles" title="New Chat" :disabled="chatInitButtonsDisabled">
        ➕ {{ getTranslation(currentLanguage, "NEW_CONVERSATION") }}
      </button>

      <!-- Chat History List -->
      <div class="history-list-wrapper">
        <div v-for="chat in chats" :key="chat.chatID">
          <button class="chat-item selectable-chat" @click="selectChat(chat.chatID)" :class="{ 'selected': currentChatID === chat.chatID }"  :disabled="chatInitButtonsDisabled">
            {{ chat.title }}
          </button>
        </div>
      </div>
    </div>
  </aside>
</template>

<script>
import { getTheme } from "@/assets/color.js";
import { getTranslation } from "@/assets/language";

export default {
  props: ["chats", "currentChatID", "currentLanguage", "chatInitButtonsDisabled"],
  data() {
    return {
      aiServerUrl: "http://localhost:8080", // URL of the AI server
      isCollapsed: true, // Controls sidebar visibility
      currentTheme: "default", // Tracks the current theme
      themeStyles: {}, // Stores dynamic styles
    };
  },
  methods: {
    getTranslation,

    async fetchChatHistory() {
      const sessionID = localStorage.getItem("sessionID");
      if (!sessionID) {
        console.error("No sessionID found in localStorage.");
        return;
      }

      try {
        const response = await fetch(`${this.aiServerUrl}/getUserChats?sessionID=${sessionID}`);
        if (!response.ok) throw new Error("Failed to load chat history.");

        const data = await response.json();
        if (data.chatList && Array.isArray(data.chatList)) {
          this.$emit("updateChats", data.chatList);
        }
      } catch (error) {
        console.error("Error loading chat history:", error);
      }
    },



    addChat() {
      this.$emit("resetMainContent");
      this.isCollapsed = true;
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
          border: `2px solid ${theme.border}`,
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
    newChatButtonStyles() {
      return {
        ...this.buttonStyles,
        backgroundColor: "var(--button-color)",
      };
    },
  },
  mounted() {
    this.applyTheme("default");
    this.listenForThemeChange();
    this.fetchChatHistory();
  },
};
</script>


<style>
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
  padding: 4px;
  margin: 8px;
  transition: opacity 0.3s ease-in-out;
  color: black;
}

/* Lower the "New Conversation" button */
.chat-item:first-of-type {
  margin-top: 40px;
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

.chat-item {
  width: 100%;
  padding: 14px;
  cursor: pointer;
  text-align: center;
  font-weight: bold;
  transition: background-color 0.3s ease-in-out, transform 0.2s;
  margin: 10px 0;
  color: var(--text-color);
  border-radius: 10px;
  border: 2px solid var(--border-color);
}

/* Centered Chat Bubbles */
.history-list-wrapper .chat-item {
  width: 100%;
  padding: 12px;
  cursor: pointer;
  text-align: center;
  font-weight: bold;
  transition: background-color 0.3s ease-in-out, transform 0.2s;
  margin: 4px 0;
  color: var(--text-color);
  border-radius: 10px;
  border: 2px solid var(--border-color);
}

/* Default transparency except for New Conversation */
.selectable-chat {
  background-color: transparent;
}

/* Hover effect - Light Blue */
.selectable-chat:hover {
  background-color: lightblue;
  opacity: 1;
}

/* Clicked (Selected) effect - Original color */
.selectable-chat.selected {
  background-color: var(--primary-color);
}
</style>
