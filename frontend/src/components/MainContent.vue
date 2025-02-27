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
          <div v-for="(msg, index) in messages" :key="index" class="message">
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

export default {
  data() {
    return {
      userInput: "",
      userId: "", 
      currentChatID: "",
      messages: [],
      aiServerURL: "http://localhost:8080",
    };
  },
  mounted() {
    this.getUserId();
  },
  methods: {
    async getUserId() {
      try {
        const storedUsername = sessionStorage.getItem("username");
        if (!storedUsername) {
          console.error("⚠️ No username found in sessionStorage, redirecting to login.");
          this.$router.push("/login");
          return;
        }

        const response = await axios.get(`${this.aiServerURL}/getUserID`, {
          params: { username: storedUsername },
        });

        if (response.status === 200) {
          this.userId = response.data.userID;
          console.log("User ID:", this.userId);
        } else {
          console.error("User not found, redirecting to login.");
          this.$router.push("/login");
        }
      } catch (error) {
        console.error("Error fetching user ID:", error);
      }
    },

    async sendInitialMessage(message) {
      try {
        if (!this.userId) {
          console.error("User ID is missing, cannot create chat.");
          return;
        }

        const response = await axios.post(`${this.aiServerURL}/createChat`, {
          username: this.userId,
          initialMessage: message,
        });

        if (response.status !== 200) {
          throw new Error("Failed to create chat.");
        }

        this.currentChatID = response.data.chatID;
        console.log("New Chat Created, Chat ID:", this.currentChatID);

        this.messages = [{ sender: "user", content: message }];
        await this.requestChatHistory();
      } catch (error) {
        console.error("Error creating chat:", error);
      }
    },

    async sendMessage() {
      if (!this.userInput.trim()) {
        alert("⚠️ Please enter a message!");
        return;
      }

      if (!this.currentChatID || !this.userId) {
        alert("⚠️ Chat ID or User ID is missing. Please start a new chat.");
        return;
      }

      const messageToSend = this.userInput.trim();
      this.userInput = "";
      this.messages.push({ sender: "user", content: messageToSend });

      try {
        const response = await axios.post(`${this.aiServerURL}/sendMessage`, {
          username: this.userId,
          chatID: this.currentChatID,
          newMessage: messageToSend,
        });

        if (response.status !== 200) {
          throw new Error("Unexpected response code: " + response.status);
        }

        console.log("AI Response:", response.data.response);
        this.messages.push({ sender: "assistant", content: response.data.response });

        await this.requestChatHistory();
      } catch (error) {
        console.error("Error sending message:", error);
        this.messages.push({ sender: "System", content: "⚠️ Failed to send message. Please try again." });
      }

      this.scrollToBottom();
    },

    async requestChatHistory() {
      try {
        const response = await axios.get(`${this.aiServerURL}/getChatHistory`, {
          params: {
            username: this.userId,
            chatID: this.currentChatID,
          },
        });

        console.log("📢 API Response:", response.data);

        if (response.status !== 200) {
          throw new Error("Failed to fetch chat history.");
        }

        this.processChatHistory(response.data.history);
      } catch (error) {
        console.error("Error fetching chat history:", error);
      }
    },

    async processChatHistory(messageHistory) {
      let parsedMessages = [];
      let sender = "assistant";

      console.log("Processing history:", messageHistory);
      let lines = messageHistory.split("\n");

      for (let i = 0; i < lines.length; i++) {
        if (lines[i].trim() === "<|user|>") {
          sender = "user";
        } else if (lines[i].trim() === "<|assistant|>") {
          sender = "assistant";
        } else if (lines[i].trim()) {
          parsedMessages.push({ sender, content: lines[i].trim() });
        }
      }

      console.log("Parsed Messages:", parsedMessages);
      if (
        parsedMessages.length > 1 &&
        parsedMessages[0].sender === "user" &&
        parsedMessages[1].sender === "assistant" &&
        parsedMessages[0].content === parsedMessages[1].content
      ) {
        console.warn("⚠️ Duplicate message detected, removing AI's duplicated user message.");
        parsedMessages.splice(1, 1); 
      }
      this.messages = parsedMessages;
      this.$forceUpdate();
    },

    scrollToBottom() {
      this.$nextTick(() => {
        const chatContainer = this.$el.querySelector(".messages-container");
        if (chatContainer) {
          chatContainer.scrollTop = chatContainer.scrollHeight;
        }
      });
    },
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
