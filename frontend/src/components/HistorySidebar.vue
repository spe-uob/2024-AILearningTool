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
          <button :style="buttonStyles" @click="selectChat(chat.chatID)">
            {{ chat.title }}
          </button>
        </li>
      </ul>
    </div>
      <!-- Button for adding a new conversation -->
      <button class="new-conversation-btn" :style="buttonStyles" @click="addChat">
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
  props: ["chats", "currentChatID"],
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
  transition: width 0.3s ease;
  position: relative;
  display: flex;
  flex-direction: column;
}

.history-sidebar {
  width: 250px;
  background-color: #f8f9fa;
  padding: 10px;
  border-right: 1px solid #ddd;
}

/* Make the conversation history scrollable */
.history-list {
  max-height: 300px; /* Adjust based on your layout */
  overflow-y: auto;
  margin-top: 10px;
  padding-right: 5px; /* Prevents content from touching scrollbar */
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

.collapsed {
  width: 50px;
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

/* Ensure consistent button styling */
button {
  margin: 5px 0;
  padding: 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  width: 100%; /* Ensure buttons take full width */
}

button:hover {
  opacity: 0.9;
}

.history-content {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.new-conversation-btn {
  margin-top: 10px;
}
</style>
