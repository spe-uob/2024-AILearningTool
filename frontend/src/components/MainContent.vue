<template>
  <main>
    <div class="chat-area">
      <!-- Welcome Screen with Logo -->
      <div v-if="this.currentChatID.length === 0" class="welcome-container">
        <img src="../assets/logo.png" alt="Logo" class="logo" />
        <p class="welcome-text">
          {{ getTranslation(currentLanguage, 'WELCOME_TO_WATSONX_AI') }}
        </p>
        <p v-if="this.currentChatID.length === 0" class="instruction-text">
          {{ getTranslation(currentLanguage, 'SELECT_INITIAL_TOPIC') }}
        </p>

        <!-- Buttons for chat initialisation -->
        <div v-if="this.currentChatID.length === 0" class="button-container">
          <button @click="sendInitialMessage(getTranslation(currentLanguage, 'I_NEED_HELP_WITH_CHOOSING_A_COURSE'))" :disabled="chatInitButtonsDisabled">
            {{ getTranslation(currentLanguage, "I_NEED_HELP_WITH_CHOOSING_A_COURSE") }}
          </button>
          <button @click="sendInitialMessage(getTranslation(currentLanguage, 'I_NEED_HELP_WITH_PLATFORM'))" :disabled="chatInitButtonsDisabled">
            {{ getTranslation(currentLanguage, "I_NEED_HELP_WITH_PLATFORM")}}
          </button>
          <button @click="sendInitialMessage(getTranslation(currentLanguage, 'I_HAVE_QUESTIONS_ABOUT_UNI_LIFE'))" :disabled="chatInitButtonsDisabled">
            {{getTranslation(currentLanguage, "I_HAVE_QUESTIONS_ABOUT_UNI_LIFE")}}
          </button>
        </div>
      </div>

      <!-- Display all messages in the conversation -->
      <div v-if="this.currentChatID.length > 0" class="chat-container">
        <!-- Scrollable message area -->
        <div class="messages-container" ref="messagesContainer">
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
            <strong v-if="msg.sender === 'user'">{{ getTranslation(currentLanguage, "USER") }}</strong>
            <strong v-else-if="msg.sender === 'assistant'">{{ getTranslation(currentLanguage, "AI") }}</strong>
            <strong v-else>{{ msg.sender }}</strong>
            <p>{{ msg.content }}</p>
          </div>
        </div>

        <!-- Input area for user messages -->
        <div class="input-area">
          <textarea
              v-model="userInput"
              :placeholder="getTranslation(currentLanguage, 'TYPE_YOUR_MESSAGE')"
              @keypress.enter.prevent="sendMessage"
          ></textarea>
          <button @click="sendMessage" :disabled="chatInitButtonsDisabled">
            {{ getTranslation(currentLanguage, "SEND") }}
          </button>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
import axios from "axios";
import { getTheme } from "@/assets/color.js";
import {getTranslation} from "@/assets/language";

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
  props: ["messages", "chats", "currentChatID", "currentLanguage", "chatInitButtonsDisabled"],
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
    getTheme,
    getTranslation,
    /**
     * Initializes a new chat with a predefined message.
     */
    async sendInitialMessage(message) {
      this.$emit("setButtonLock", true)
      let response = await fetch(this.aiServerURL + "/createChat?" + new URLSearchParams({
        "initialMessage": message
      }),{
            method: "GET",
            credentials: "include",
          }
      );

      if (!response.ok) {
        throw new Error("Non-200 backend API response");
      }

      this.$emit("updateChatID", await response.text());
      await this.$nextTick(() => {
        this.$emit("addChat", this.currentChatID, message);
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
      this.$emit("setButtonLock", false)
    },

    /**
     * Sends a user message to the AI server and processes the response.
     */
    async sendMessage() {
      if (!this.userInput.trim()) {
        alert(
            getTranslation(localStorage.getItem("langCode"), "PLEASE_ENTER_A_MESSAGE")
        );
        return;
      }

      // "if" below can be removed later
      if (!this.currentChatID) {
        alert("ChatId is not set. Please start a new chat.");
        return;
      }

      this.$emit("addMessage", "user", this.userInput);
      this.$emit("setButtonLock", true)

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
        this.$emit("addMessage", "System",
            localStorage.getItem("langCode"), "FAILED_TO_SEND_MESSAGE");
      }
      this.scrollToBottom();
    },
    
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesContainer;
        if (container) {
          container.scrollTo({ top: container.scrollHeight, behavior: "smooth" });
        }
      });
    },
  }
};
</script>


<style>
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
  width: 220px;
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
