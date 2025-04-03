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
        <div v-for="chat in chats" :key="chat.chatID" class="chat-item-container">
          <div
            class="chat-item selectable-chat"
            :class="{ selected: chat.chatID === currentChatID }"
            @click="selectChat(chat.chatID)"
          >
            {{ chat.title }}
          </div>
          <!-- Add three dots menu button -->
          <button class="three-dots-btn" @click.stop="toggleOptions(chat.chatID)">
            <i class="fa fa-ellipsis-v"></i>
          </button>
          
          <!-- Options menu -->
          <div v-if="showOptionsFor === chat.chatID" class="options-menu">
            <button @click.stop="exportChat(chat.chatID, chat.title)">
              <i class="fa fa-download"></i> Export
            </button>
          </div>
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
      showOptionsFor: null, // Track which chat's options are visible
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
    async exportChat(chatID, title) {
      try {
        const sessionID = localStorage.getItem("sessionID");
        if (!sessionID) {
          console.error("No sessionID found in localStorage.");
          return;
        }
        
        // Fetch chat history for the specific chat
        const response = await fetch(`${this.aiServerUrl}/getChatHistory`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ sessionID, chatID })
        });
        
        if (!response.ok) {
          throw new Error(`Failed to fetch chat history for export. Status: ${response.status}`);
        }
        
        const data = await response.json();
        console.log("Received chat data:", data);
        
        // Handle the case where history is a JSON string
        let messages = [];
        if (data.history && typeof data.history === 'string') {
          try {
            messages = JSON.parse(data.history);
          } catch (e) {
            console.error("Failed to parse history JSON:", e);
            throw new Error("Invalid chat history format");
          }
        } else if (data.messages && Array.isArray(data.messages)) {
          messages = data.messages;
        } else if (Array.isArray(data)) {
          messages = data;
        } else {
          console.error("Unexpected data format:", data);
          throw new Error("Invalid chat data format.");
        }
        
        if (!Array.isArray(messages) || messages.length === 0) {
          throw new Error("No messages found in chat history");
        }
        
        // Format the chat messages
        let content = `# ${title}\n\n`;
        
        messages.forEach(msg => {
          // Handle different possible message formats
          const sender = msg.sender || msg.role || msg.user || "Unknown";
          const messageContent = msg.content || msg.message || msg.text || "";
          const role = sender.toLowerCase() === "user" ? "User" : "Assistant";
          
          content += `## ${role}:\n${messageContent}\n\n`;
        });
        
        // Create and download the file
        const blob = new Blob([content], { type: "text/plain" });
        const url = URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = `${title.replace(/[^a-z0-9]/gi, '_')}_chat.txt`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
        
        // Close the options menu
        this.showOptionsFor = null;
      } catch (error) {
        console.error("Error exporting chat:", error);
        alert("Failed to export chat. Please try again.");
      }
    },
    toggleOptions(chatID) {
      this.showOptionsFor = this.showOptionsFor === chatID ? null : chatID;
    },
    // Close options menu when clicking outside
    closeOptionsMenu() {
      this.showOptionsFor = null;
    }
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
    
    // Add event listener to close options menu when clicking outside
    document.addEventListener('click', this.closeOptionsMenu);
  },
  beforeUnmount() {
    // Remove event listener
    document.removeEventListener('click', this.closeOptionsMenu);
  }
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

/* Three dots button styles */
.chat-item-container {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

/* Three dots button styles */
.three-dots-btn {
  background: transparent;
  border: none;
  color: var(--text-color);
  cursor: pointer;
  padding: 5px;
  margin-left: 5px;
  opacity: 0.6;
  transition: opacity 0.3s;
}

.three-dots-btn:hover {
  opacity: 1;
}

/* Options menu styles */
.options-menu {
  position: absolute;
  right: 0;
  top: 100%;
  background-color: var(--background-color);
  border: 1px solid var(--border-color);
  border-radius: 5px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 20;
  min-width: 120px;
}

.options-menu button {
  display: block;
  width: 100%;
  text-align: left;
  padding: 8px 12px;
  background: none;
  border: none;
  cursor: pointer;
  color: var(--text-color);
  transition: background-color 0.2s;
}

.options-menu button:hover {
  background-color: var(--accent-color);
}

.options-menu button i {
  margin-right: 8px;
}
</style>
