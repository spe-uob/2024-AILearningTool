<template>
  <aside :class="{'collapsed': isCollapsed}" :style="asideStyles">
    <!-- Toggle Button for Collapsing/Expanding -->
    <button :style="buttonStyles" @click="toggleSidebar">History</button>

    <!-- History content, only visible when not collapsed -->
    <div v-if="!isCollapsed" class="history-content">
      <h3 :style="textStyles">Conversation History</h3>
      <ul>
        <!-- Dynamically generated chat log button -->
        <li v-for="conversation in conversations" :key="conversation.chatId">
          <button :style="buttonStyles" @click="loadConversation(conversation.chatId)">
            {{ conversation.chatId }}
          </button>
        </li>
      </ul>

      <!-- Button for adding a new conversation -->
      <button
        class="new-conversation-btn"
        :style="buttonStyles"
        @click="createNewConversation"
      >
        ➕ New Conversation
      </button>
    </div>
  </aside>
</template>

<script>
import axios from "axios";
import { getTheme } from "@/color.js";

export default {
  data() {
    return {
      isCollapsed: false, // Controls the collapse state of the sidebar
      conversations: [], // List of conversations (chatId)
      currentTheme: "default", // Tracks the current theme
      themeStyles: {}, // Stores styles for the theme
    };
  },
  methods: {
    toggleSidebar() {
      this.isCollapsed = !this.isCollapsed;
    },

    // Create new conversations and dynamically add them to the session list
    async createNewConversation() {
      try {
        const response = await axios.get(
          "https://ailearningtool.ddns.net:8080/createChat",
          {
            params: {
              userId: localStorage.getItem("userId"), // Get userId from LocalStorage
              initialMessage: "", // Empty initial content
            },
          }
        );

        const chatId = response.data.chatId; // Assuming the return field is chatId
        console.log("Created chat with ID:", chatId);

        // Dynamically added to the session list
        this.conversations.push({ chatId });
      } catch (error) {
        console.error("Failed to create a new conversation:", error);
        alert("Failed to create a new conversation. Please try again.");
      }
    },

    // Load the contents of the specified session
    async loadConversation(chatId) {
      try {
        const response = await axios.get(
          "https://ailearningtool.ddns.net:8080/getChatHistory",
          {
            params: {
              userId: localStorage.getItem("userId"), // Get userId from LocalStorage
              chatId, // The currently selected chatId
            },
          }
        );

        const chatHistory = response.data.history; // Assuming the return field is history
        console.log("Chat History:", chatHistory);

        // Pass the chat content to the parent component (requires the parent component to listen to this event)
        this.$emit("loadConversation", chatHistory);
      } catch (error) {
        console.error("Failed to load conversation:", error);
        alert("Failed to load the conversation. Please try again.");
      }
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
  },
};
</script>

<style scoped>
aside {
  width: 250px;
  padding: 10px;
  transition: width 0.3s ease;
  position: relative;
}

.collapsed {
  width: 50px;
}

button {
  margin: 5px 0;
  padding: 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  opacity: 0.9;
}

.history-content {
  margin-top: 10px;
}

.new-conversation-btn {
  margin-top: 10px;
}
</style>
