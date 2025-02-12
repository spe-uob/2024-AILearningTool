<template>
  <main>
    <div class="chat-area">
      <!-- Welcome Screen with Logo -->
      <div v-if="this.currentChatID.length === 0" class="welcome-container">
        <img src="../assets/logo.png" alt="Logo" class="logo" />
        <p class="welcome-text">Welcome to Watsonx AI!</p>
        <p class="instruction-text">Select one of the topics below:</p>

        <!-- Buttons for initiating a new chat -->
        <div class="button-container">
          <button @click="sendInitialMessage('I need help with choosing a course')">
            I need help with choosing a course
          </button>
          <button @click="sendInitialMessage('I need help with IBM SkillsBuild platform')">
            I need help with IBM SkillsBuild platform
          </button>
          <button @click="sendInitialMessage('I have questions about university life')">
            I have questions about university life
          </button>
        </div>
      </div>

      <!-- Display all messages in the conversation -->
      <div v-if="this.currentChatID.length > 0" class="chat-container">
        <!-- Scrollable message area -->
        <div class="messages-container">
          <div
              v-for="(msg, index) in messages"
              :key="index"
              class="message"
              :class="{
              'user-message': msg.sender === 'user',
              'assistant-message': msg.sender === 'assistant',
              'system-message': msg.sender === 'System'
            }"
          >
            <strong v-if="msg.sender === 'user'">User</strong>
            <strong v-else-if="msg.sender === 'assistant'">AI</strong>
            <strong v-else>{{ msg.sender }}</strong>
            <p>{{ msg.content }}</p>
          </div>
        </div>

        <!-- Input area for user messages -->
        <div class="input-area">
          <textarea
              v-model="userInput"
              placeholder="Type your message..."
              @keypress.enter.prevent="sendMessage"
          ></textarea>
          <button @click="sendMessage">Send</button>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
import axios from "axios";
import { getTheme } from "../assets/color.js";

export default {
  data() {
    return {
      userInput: "",
      currentTopic: "",
      currentTurn: "user", // Tracks conversation turn (user or AI)
      userId: localStorage.getItem("userId") || "",
      aiServerURL: "http://localhost:8080", // API endpoint for AI server
      currentTheme: "default", // Stores the current UI theme
    };
  },
  props: ["messages", "chats", "currentChatID"],
  watch: {
    // Automatically scroll to the bottom when new messages arrive
    messages() {
      if (this.messages.length === 0 && this.currentChatID.length > 0) {
        this.currentTurn = "user";
        this.requestChatHistory();
      }
      this.$nextTick(() => {
        this.scrollToBottom();
      });
    }
  },
  methods: {
    /**
     * Initializes a new chat with a predefined message.
     */
    async sendInitialMessage(message) {
      let response = await fetch(
          this.aiServerURL +
          "/createChat?" +
          new URLSearchParams({
            initialMessage: message,
          }),
          {
            method: "GET",
            credentials: "include",
          }
      );

      if (!response.ok) {
        throw new Error("Non-200 backend API response");
      }

      const chatID = await response.text();  
      this.$emit("updateChatID", chatID);
      this.$nextTick(() => {
        this.currentChatID = chatID;  // Manually update currentChatID
        this.$emit("addChat", chatID, message);
        this.requestChatHistory();
      });
    },

    /**
     * Requests chat history from the server based on the current chat ID.
     */
    requestChatHistory() {
      fetch(
          this.aiServerURL +
          "/getChatHistory?" +
          new URLSearchParams({
            chatID: this.currentChatID,
          }),
          {
            method: "GET",
            credentials: "include",
          }
      ).then(async (response) => {
        if (!response.ok) {
          throw new Error("Non-200 backend API response");
        } else {
          this.processChatHistory(await response.text());
        }
      });
    },

    /**
     * Processes retrieved chat history and structures it for display.
     */
    async processChatHistory(messageHistory) {
      messageHistory = String(messageHistory);
      let currentRole;
      let nextRole;
      let nextRoleIndex;
      let currentMessage;
      for (let i = 0; ; i++) {
        if (i === 0) {
          currentRole = "<|system|>";
          nextRole = "<|user|>";
        } else if (i % 2 !== 0) {
          currentRole = "<|user|>";
          nextRole = "<|assistant|>";
        } else {
          currentRole = "<|assistant|>";
          nextRole = "<|user|>";
        }
        if (messageHistory.includes(currentRole)) {
          messageHistory = messageHistory.substring(
              messageHistory.indexOf(currentRole) + currentRole.length
          );
          nextRoleIndex = messageHistory.indexOf(nextRole);
          if (nextRoleIndex !== -1) {
            currentMessage = messageHistory.substring(
                0,
                messageHistory.indexOf(nextRole)
            );
          } else {
            currentMessage = messageHistory;
          }
          if (currentRole === "<|system|>") {
            continue;
          }
          this.$emit("addMessage", this.currentTurn, currentMessage);
          this.currentTurn = this.currentTurn === "user" ? "assistant" : "user";
        } else {
          break;
        }
      }
    },

    /**
     * Sends a user message to the AI server and processes the response.
     */
    async sendMessage() {
      if (!this.userInput.trim()) {
        alert("Please enter a message!");
        return;
      }

      if (!this.currentChatID) {
        alert("ChatId is not set. Please start a new chat.");
        return;
      }

      this.$emit("addMessage", "user", this.userInput);

      const messageToSend = this.userInput.trim();
      this.userInput = "";

      try {
        const response = await axios.get(this.aiServerURL + "/sendMessage", {
          params: {
            userID: this.userId,
            chatID: this.currentChatID,
            newMessage: messageToSend,
          },
          withCredentials: true,
        });

        if (response.status !== 200) {
          throw new Error(`Unexpected response code: ${response.status}`);
        }

        this.$emit("addMessage", "assistant", response.data);
      } catch (error) {
        console.error("Error sending message:", error);
        this.$emit("addMessage", "System", "Failed to send message. Please try again.");
      }
      this.scrollToBottom();
    },
  }
};
</script>


<style scoped>
/* Adjust the main area height to make the chat window more compact */
main {
  flex-grow: 1;
  padding: 20px;
  background-color: var(--background-color);
  color: var(--text-color);
  display: flex;
  flex-direction: column;
  height: calc(100vh - 40px);
}

.chat-area {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  background-color: var(--background-color);
  padding: 20px;
  border-radius: 12px;
  border: 1px solid var(--border-color);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.chat-container {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  overflow: hidden;
}

.messages-container {
  flex-grow: 1;
  overflow-y: auto;
  padding: 12px;
  border-radius: 12px;
  border: 2px solid var(--border-color);
  background-color: var(--background-color);
  box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease-in-out;
}

.messages-container::-webkit-scrollbar {
  width: 6px;
}
.messages-container::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}
.messages-container::-webkit-scrollbar-track {
  background: transparent;
}

/* General message bubble style */
.message {
  margin: 10px 0;
  padding: 12px 16px;
  border-radius: 16px;
  max-width: 60%;
  white-space: pre-wrap;
  word-wrap: break-word;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.12);
  position: relative;  /* For the chat bubble tail */
  animation: fadeIn 0.3s forwards ease-in-out;
  opacity: 0;
  transform: translateY(10px);
}

/* User messages (right side) */
.user-message {
  align-self: flex-end;
  background-color: var(--primary-color);
  border: 1px solid var(--secondary-color);
  margin-left: auto;
}
.user-message::after {
  content: "";
  position: absolute;
  bottom: 10px;
  right: -8px;
  width: 0;
  height: 0;
  border: 8px solid transparent;
  border-left-color: var(--primary-color);
}

/* AI messages (left side) */
.assistant-message {
  align-self: flex-start;
  background-color: var(--accent-color);
  border: 1px solid var(--border-color);
  margin-right: auto;
}
.assistant-message::after {
  content: "";
  position: absolute;
  bottom: 10px;
  left: -8px;
  width: 0;
  height: 0;
  border: 8px solid transparent;
  border-right-color: var(--accent-color);
}

/* System messages/errors */
.system-message {
  align-self: center;
  background-color: var(--border-color);
  border: 1px solid var(--secondary-color);
  max-width: 40%;
  margin: 5px auto;
  text-align: center;
}

@keyframes fadeIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.input-area {
  display: flex;
  flex-direction: column;
  background: var(--background-color);
  padding: 12px;
  border-top: 2px solid var(--border-color);
  box-shadow: 0 -4px 6px rgba(0, 0, 0, 0.1);
}

textarea {
  width: 100%;
  height: 80px;
  resize: none;
  padding: 12px;
  box-sizing: border-box;
  font-size: 14px;
  background-color: var(--background-color);
  color: var(--text-color);
  border-radius: 12px;
  border: 2px solid var(--border-color);
  transition: border-color 0.3s ease-in-out, box-shadow 0.3s ease-in-out;
}

textarea:focus {
  outline: none;
  border-color: var(--accent-color);
  box-shadow: 0 0 10px var(--accent-color);
}

button {
  margin-top: 10px;
  padding: 12px;
  background-color: var(--button-color);
  color: var(--text-color);
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-weight: bold;
  transition: transform 0.2s ease-in-out, box-shadow 0.3s ease-in-out;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.12);
}

.welcome-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 40px;
  height: 100vh;
}

.logo {
  width: 160px;
  height: auto;
  margin-bottom: 20px;
}

.welcome-text {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 15px;
}

.instruction-text {
  font-size: 20px;
  color: #666;
  margin-bottom: 25px;
}

.button-container {
  display: flex;
  flex-direction: row;
  justify-content: center;
  gap: 20px;
  width: 100%;
  max-width: 800px;
}

.button-container button {
  flex: 1;
  padding: 14px;
  font-size: 16px;
  font-weight: bold;
  background-color: var(--button-color);
  color: var(--text-color);
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: transform 0.2s ease-in-out, box-shadow 0.3s ease-in-out;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.12);
}

.button-container button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.18);
}

.button-container button:active {
  transform: scale(0.96);
}

</style>
