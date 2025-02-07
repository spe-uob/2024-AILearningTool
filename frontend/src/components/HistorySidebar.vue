<template>
  <aside :class="{'collapsed': isCollapsed}" :style="asideStyles">
    <!-- Toggle Button for Collapsing/Expanding -->
    <button :style="buttonStyles" @click="toggleSidebar">History</button>

    <!-- History content, only visible when not collapsed -->
    <div v-if="!isCollapsed" class="history-content">
      <h3 :style="textStyles">Conversation History</h3>

      <!-- Scrollable conversation list -->
      <div class="history-list">
      <ul>
        <li v-for="chat in this.chats" :key="chat.chatID">
          <button :style="buttonStyles" @click="selectChat(chat.chatID)" :disabled="chatInitButtonsDisabled">
            {{ chat.title }}
          </button>
        </li>
      </ul>
    </div>
      <!-- Button for adding a new conversation -->
      <button class="new-conversation-btn" :style="buttonStyles" @click="addChat" :disabled="chatInitButtonsDisabled">
        ➕ New Conversation
      </button>
    </div>
  </aside>
</template>

<script>
import { getTheme } from "../assets/color.js";

export default {
  data() {
    return {
      isCollapsed: false, // Controls the collapse state of the sidebar
      aiServerUrl: "http://localhost:8080",
      currentTheme: "default", // Tracks the current theme
      themeStyles: {}, // Stores styles for the theme
    };
  },
  props: ["chats", "currentChatID", "chatInitButtonsDisabled"],
  methods: {
    // Reverts the state of MainContent to initial state.
    async addChat() {
      this.$emit("resetMainContent")
    },

    toggleSidebar() {
      this.isCollapsed = !this.isCollapsed;
    },

    // Sends a chat selection event, asks other components to render the required chat
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
    // Listen for theme change events
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
    // Apply the default theme when the component is mounted
    this.applyTheme("default");

    // Start listening for theme changes
    this.listenForThemeChange();
  },
};
</script>

<style scoped>
aside {
  width: 250px;
  padding: 10px;
  transition: width 0.3s ease-in-out, box-shadow 0.3s ease-in-out, background-color 0.3s ease-in-out;
  position: relative;
  display: flex;
  flex-direction: column;
  border-radius: 12px;
  box-shadow: 3px 0 12px rgba(0, 0, 0, 0.1);
}

.collapsed {
  width: 60px;
  box-shadow: none;
}

.history-list {
  max-height: 300px;
  overflow-y: auto;
  margin-top: 10px;
  padding-right: 5px;
  border-radius: 8px;
  box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease-in-out;
}

.history-list::-webkit-scrollbar {
  width: 6px;
}

.history-list::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.history-list::-webkit-scrollbar-track {
  background: transparent;
}

button {
  margin: 5px 0;
  padding: 12px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  width: 100%;
  font-weight: bold;
  transition: transform 0.2s ease-in-out, box-shadow 0.3s ease-in-out;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.12);
}

button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.18);
}

button:active {
  transform: scale(0.96);
}

.history-content {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  opacity: 0;
  transform: translateY(10px);
  animation: fadeIn 0.4s forwards ease-in-out;
}

@keyframes fadeIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.new-conversation-btn {
  margin-top: 12px;
}

</style>
