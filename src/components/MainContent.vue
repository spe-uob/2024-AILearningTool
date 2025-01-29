<template>
  <main>
    <div class="chat-area">
      <p>Welcome to Watsonx AI!</p>
      <p v-if="!topicSelected">Select one of the topics below:</p>

      <!-- Buttons for selecting topics -->
      <div v-if="!topicSelected">
        <button @click="startTopic('First Time Coming to University')">First Time Coming to University</button>
        <button @click="startTopic('Academic Inquiry')">Academic Inquiry</button>
      </div>

      <!-- Chatbox -->
      <div v-if="topicSelected" class="chat-container">
        <!-- Scrollable message area -->
        <div class="messages-container">
          <div v-for="msg in processedMessages" :key="msg.id" class="message">
            <strong v-if="msg.sender === 'AI'">AI:</strong>
            <strong v-else>You:</strong>
            <p>{{ msg.content }}</p>
          </div>
        </div>

        <!-- Fixed input area -->
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
import { getTheme } from "@/color.js";

export default {
  data() {
    return {
      userInput: "", // User input content
      messages: [], // Chat log
      topicSelected: false, // Whether or not the topic is chosen
      currentTopic: "", // Currently Selected Topic
      userId: localStorage.getItem("userId") || "", // Get user ID from LocalStorage
      chatId: localStorage.getItem("chatId") || "", // Get the dialog ID from LocalStorage
      aiServerUrl: "https://ailearningtool.ddns.net:8080/sendMessage", // AI server address
      currentTheme: "default", // Current theme for the component
    };
  },
  computed: {
    processedMessages() {
      return this.messages.map((msg) => ({
        ...msg,
        content: this.wrapText(msg.content, 1000),
      }));
    },
  },
  methods: {
    wrapText(text, maxLength) {
      let result = "";
      let currentLine = "";
      const words = text.split(" ");

      words.forEach((word) => {
        if ((currentLine + word).length > maxLength) {
          result += currentLine.trim() + "\n";
          currentLine = word + " ";
        } else {
          currentLine += word + " ";
        }
      });

      result += currentLine.trim();
      return result;
    },
    async sendMessage() {
      if (!this.userInput.trim()) return;

      // Adding User Input to the Chat Log
      this.messages.push({
        id: Date.now(),
        sender: "You",
        content: this.userInput,
      });

      const userMessage = this.userInput; // Cache user input
      this.userInput = ""; // Empty the input box

      this.$nextTick(() => {
        this.scrollToBottom();
      });

      try {
        // Send a message to the AI server
        const response = await axios.get(this.aiServerUrl, {
          params: {
            userId: this.userId, // Current User ID
            message: userMessage, // User-entered text
            chatId: this.chatId, // Current dialogue ID
          },
        });

        // Adding AI replies to chats
        this.messages.push({
          id: Date.now() + 1,
          sender: "AI",
          content: response.data.responseText, // Assuming the return field is `responseText`
        });

        this.$nextTick(() => {
          this.scrollToBottom();
        });
      } catch (error) {
        console.error("Error communicating with AI server:", error);
        this.messages.push({
          id: Date.now() + 1,
          sender: "AI",
          content: "Sorry, there was an error connecting to the server.",
        });

        this.$nextTick(() => {
          this.scrollToBottom();
        });
      }
    },
    startTopic(topic) {
      this.topicSelected = true;
      this.currentTopic = topic;

      this.messages.push({
        id: Date.now(),
        sender: "system",
        content: `You have selected the topic: ${topic}`,
      });

      this.chatId = `chat_${Date.now()}`;
      localStorage.setItem("chatId", this.chatId);
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
  },
  mounted() {
    this.applyTheme(this.currentTheme);
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
  height: 100vh; /* Full viewport height */
}

.chat-area {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  background-color: var(--background-color);
  padding: 20px;
  border-radius: 5px;
  border: 1px solid var(--border-color);
  overflow: hidden; /* Prevents chat overflow */
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
  padding: 10px;
  border-radius: 5px;
  border: 1px solid var(--border-color);
  background-color: var(--background-color);
  max-height: calc(100vh - 180px); /* Ensure it fits with input area */
}

.message {
  margin: 10px 0;
  padding: 10px;
  border-radius: 15px;
  max-width: 60%;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.message:nth-child(odd) {
  align-self: flex-start;
  background-color: var(--primary-color);
  border-color: var(--secondary-color);
}

.message:nth-child(even) {
  align-self: flex-end;
  background-color: var(--accent-color);
  border-color: var(--border-color);
}

.input-area {
  display: flex;
  flex-direction: column;
  background: var(--background-color);
  padding: 10px;
  border-top: 1px solid var(--border-color);
}

textarea {
  width: 100%;
  height: 80px;
  resize: none;
  padding: 5px;
  box-sizing: border-box;
  font-size: 14px;
  background-color: var(--background-color);
  color: var(--text-color);
}

textarea:focus {
  outline: none;
}

button {
  margin-top: 10px;
  padding: 10px;
  background-color: var(--button-color);
  color: var(--text-color);
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: var(--accent-color);
}
</style>
