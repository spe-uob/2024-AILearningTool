<template>
  <main>
    <div class="chat-area">
      <p>Welcome to Watsonx AI!</p>

      <p v-if="!currentChatID">Select one of the topics below:</p>

      <div v-if="!currentChatID">
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

      <div v-if="currentChatID" class="chat-container">
        <div class="messages-container">
          <div v-for="msg in messages" :key="msg.id" class="message">
            <strong v-if="msg.sender === 'user'">User</strong>
            <strong v-else>AI</strong>
            <p>{{ msg.content }}</p>
          </div>
        </div>

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
      currentTurn: "user",
      userId: "",
      aiServerURL: "http://localhost:8080",
      currentTheme: "default",
    };
  },
  props: ["messages", "chats", "currentChatID"],
  watch: {
    messages() {
      if (this.messages.length === 0 && this.currentChatID) {
        this.currentTurn = "user";
        this.requestChatHistory();
      }
    },
  },
  methods: {
    async sendInitialMessage(message) {
      try {
        const response = await fetch(`${this.aiServerURL}/createChat?${new URLSearchParams({ initialMessage: message })}`, {
          method: "GET",
          credentials: "include",
        });

        if (!response.ok) {
          throw new Error("Failed to create chat.");
        }

        this.$emit("updateChatID", await response.text());
        await this.$nextTick(() => {
          this.$emit("addChat", this.currentChatID, message);
          this.requestChatHistory();
        });
      } catch (error) {
        console.error("Error creating chat:", error);
      }
    },

    async requestChatHistory() {
      try {
        const response = await fetch(`${this.aiServerURL}/getChatHistory?${new URLSearchParams({ chatID: this.currentChatID })}`, {
          method: "GET",
          credentials: "include",
        });

        if (!response.ok) {
          throw new Error("Failed to fetch chat history.");
        }

        this.processChatHistory(await response.text());
      } catch (error) {
        console.error("Error fetching chat history:", error);
      }
    },

    async processChatHistory(messageHistory) {
      let currentRole, openAIRole, nextRole, nextRoleIndex, currentMessage;
      for (let i = 0; ; i++) {
        if (i === 0) {
          currentRole = "<|system|>";
          openAIRole = "system";
          nextRole = "<|user|>";
        } else if (i % 2 !== 0) {
          currentRole = "<|user|>";
          openAIRole = "user";
          nextRole = "<|assistant|>";
        } else {
          currentRole = "<|assistant|>";
          openAIRole = "assistant";
          nextRole = "<|user|>";
        }

        if (messageHistory.includes(currentRole)) {
          messageHistory = messageHistory.substring(messageHistory.indexOf(currentRole) + currentRole.length);
          nextRoleIndex = messageHistory.indexOf(nextRole);
          currentMessage = nextRoleIndex !== -1 ? messageHistory.substring(0, nextRoleIndex) : messageHistory;

          if (currentRole !== "<|system|>") {
            this.$emit("addMessage", this.currentTurn, currentMessage);
            this.currentTurn = this.currentTurn === "user" ? "assistant" : "user";
          }
        } else {
          break;
        }
      }
    },

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
        const response = await axios.get(`${this.aiServerURL}/sendMessage`, {
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

    applyTheme(themeName) {
      const theme = getTheme(themeName);
      Object.keys(theme).forEach((key) => {
        document.documentElement.style.setProperty(`--${key}-color`, theme[key]);
      });
    },

    scrollToBottom() {
      const chatContainer = this.$el.querySelector(".messages-container");
      if (chatContainer) {
        chatContainer.scrollTop = chatContainer.scrollHeight;
      }
    },

    async getUserId() {
      try {
        const response = await axios.get(`${this.aiServerURL}/signup`, { withCredentials: true });
        if (response.status === 200) {
          this.userId = response.data;
        } else {
          console.error("Failed to get userId.");
        }
      } catch (error) {
        console.error("Error fetching userId:", error);
      }
    },
  },

  async mounted() {
    if (!this.userId) {
      await this.getUserId();
    }
  },
};
</script>


<style scoped>
main {
  flex-grow: 1;
  padding: 20px;
  background-color: var(--background-color);
  color: var(--text-color);
  display: flex;
  flex-direction: column;
  height: 100vh;
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
  max-height: calc(100vh - 180px);
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

.message {
  margin: 10px 0;
  padding: 14px;
  border-radius: 20px;
  max-width: 75%;
  white-space: pre-wrap;
  word-wrap: break-word;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.12);
  opacity: 0;
  transform: translateY(10px);
  animation: fadeIn 0.3s forwards ease-in-out;
}

.message:nth-child(odd) {
  align-self: flex-start;
  background-color: var(--primary-color);
  border: 1px solid var(--secondary-color);
}

.message:nth-child(even) {
  align-self: flex-end;
  background-color: var(--accent-color);
  border: 1px solid var(--border-color);
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

button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.18);
}

button:active {
  transform: scale(0.96);
}

</style>
