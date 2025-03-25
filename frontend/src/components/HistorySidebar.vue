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
          <div class="chat-item-container">
            <button class="chat-item selectable-chat" @click="selectChat(chat.chatID)" :class="{ 'selected': currentChatID === chat.chatID }" :disabled="chatInitButtonsDisabled">
              {{ chat.title }}
            </button>
            <button class="download-btn" @click="downloadChat(chat)" title="Download Chat History">
              ⬇️
            </button>
          </div>
        </div>
      </div>
      
      
      <style scoped>
      .chat-item-container {
        display: flex;
        align-items: center;
        gap: 8px;
        margin: 4px 0;
      }
      
      .chat-item {
        flex: 1;
      }
      
      .download-btn {
        padding: 8px;
        background: transparent;
        border: none;
        cursor: pointer;
        font-size: 16px;
        border-radius: 50%;
        transition: background-color 0.3s;
      }
      
      .download-btn:hover {
        background-color: var(--border-color);
      }
      </style>
    </div>
  </aside>
</template>

<script>
import { getTheme } from "../assets/color.js";
import { getTranslation } from "@/assets/language";
import { BACKEND_URL } from "@/assets/globalConstants"

export default {
  props: ["chats", "currentChatID", "currentLanguage", "chatInitButtonsDisabled"],
  data() {
    return {
      isCollapsed: false, // Controls sidebar visibility
      currentTheme: "default", // Tracks the current theme
      themeStyles: {}, // Stores dynamic styles
    };
  },
  methods: {
    getTranslation,
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
          border: `2px solid ${theme.border}`,
        },
      };
    },
    listenForThemeChange() {
      window.addEventListener("themeChange", (event) => {
        this.applyTheme(event.detail.themeName);
      });
    },
    async downloadChat(chat) {
      try {
        const response = await fetch(
          BACKEND_URL + "/getChatHistory?" +
          new URLSearchParams({
            chatID: chat.chatID,
          }),
          {
            method: "GET",
            credentials: "include",
          }
        );

        if (!response.ok) {
          throw new Error("Failed to fetch chat history");
        }

        const messages = await response.json();
        
        // Format the chat history
        const formattedChat = messages.map((msg, index) => {
          const role = index % 2 === 0 ? "User" : "AI";
          return `${role}: ${msg.content}\n`;
        }).join('\n');

        // Create a download link
        const blob = new Blob([formattedChat], { type: 'text/plain' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `chat-${chat.chatID}.txt`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
      } catch (error) {
        console.error("Error downloading chat:", error);
        alert("Failed to download chat history");
      }
    },
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
