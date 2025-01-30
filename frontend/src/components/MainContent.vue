<template>
  <main>
    <div class="chat-area">
      <p>Welcome to Watsonx AI!</p>
      <p v-if="!topicSelected">Select one of the topics below:</p>

      <!-- 话题选择按钮 -->
      <div v-if="!topicSelected">
        <button @click="sendInitialMessage('First Time Coming to University')">
          First Time Coming to University
        </button>
        <button @click="sendInitialMessage('Academic Inquiry')">Academic Inquiry</button>
      </div>

      <!-- All messages of the conversation -->
      <div v-if="topicSelected">
        <!-- One "message bubble" -->
        <div
            v-for="msg in messages"
            :key="msg.id"
            class="message"
            :class="{ 'from-ai': msg.sender === 'assistant', 'from-user': msg.sender === 'user' }"
        >
          <strong v-if="msg.sender === 'user'">User</strong>
          <strong v-else>AI</strong>
          <p>{{ msg.content }}</p>
        </div>

        <!-- Text box for entering messages, for user -->
        <textarea
            v-model="userInput"
            placeholder="Type your message..."
            @keypress.enter.prevent="sendMessage"
        ></textarea>

        <button @click="sendMessage">Send</button>
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
      messages: [],
      topicSelected: false,
      currentTopic: "",
      userId: localStorage.getItem("userId") || "",
      currentChatID: "",
      currentTurn: "user",
      aiServerURL: "http://localhost:8080",
    };
  },

  methods: {
    // Used to reset MainContent component (eg. when "Add chat" button is clicked
    resetMainContent() {
      this.messages = []
      this.topicSelected = false
      this.userInput = ""
    },
    // Send the first message, create a chat
    async sendInitialMessage(message) {
      this.topicSelected = true
      let response = await fetch(this.aiServerURL + "/createChat?" + new URLSearchParams({
        "initialMessage": message
      }),{
            method: "GET",
            credentials: "include",
      });

      if (!response.ok) {
        throw new Error("Non-200 backend API response");
      }

      this.currentChatID = await response.text();

      this.requestChatHistory()
    },

    requestChatHistory() {
      fetch(this.aiServerURL + "/getChatHistory?" + new URLSearchParams({
        "chatID": this.currentChatID
      }),
          {
            method: "GET",
            credentials: "include",
          }
      ).then(async response => {
        if (!response.ok) {
          throw new Error("Non-200 backend API response");
        } else {
          this.processChatHistory(await response.text())
        }
      })
    },

    processChatHistory(messageHistory) {
      messageHistory = String(messageHistory)
      let currentRole;
      let openAIRole;
      let nextRole;
      let nextRoleIndex;
      let currentMessage;
      for (let i = 0; ; i++) {
        if (i === 0) {
          // Parameters for parsing first message in chat history (system prompt)
          currentRole = "<|system|>";
          openAIRole = "system";
          nextRole = "<|user|>";
        } else if (i % 2 !== 0) {
          // Parameters for parsing a user message
          currentRole = "<|user|>";
          openAIRole = "user";
          nextRole = "<|assistant|>";
        } else {
          // Parameters for parsing an AI message
          currentRole = "<|assistant|>";
          openAIRole = "assistant";
          nextRole = "<|user|>";
        }
        if (messageHistory.includes(currentRole)) {
          // Removing previous messages
          messageHistory = messageHistory.substring(messageHistory.indexOf(currentRole) + currentRole.length);
          // Parsing a message
          nextRoleIndex = messageHistory.indexOf(nextRole);
          if (nextRoleIndex !== -1) {
            currentMessage = messageHistory.substring(0, messageHistory.indexOf(nextRole));
          } else {
            currentMessage = messageHistory;
          }
          if (currentRole === "<|system|>") {
            continue
          }
          this.messages.push({
            sender: this.currentTurn,
            content: currentMessage
          })
          if (this.currentTurn === "user") {
            this.currentTurn = "assistant"
          } else {
            this.currentTurn = "user"
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

      const userMessage = {
        sender: "user",
        content: this.userInput,
      };
      this.messages.push(userMessage);

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

        this.messages.push({
          sender: "assistant",
          content: response.data || "No response from AI.",
        });
      } catch (error) {
        console.error("Error sending message:", error);

        this.messages.push({
          sender: "System",
          content: "Failed to send message. Please try again.",
        });
      }
    },
  },

    // Former stuff
    async getUserId() {
      try {
        const response = await axios.get(this.aiServerURL + "/signup", { withCredentials: true });
        if (response.status === 200) {
          this.userId = response.data;
          localStorage.setItem("userId", this.userId);
        } else {
          console.error("Failed to get userId.");
        }
      } catch (error) {
        console.error("Error fetching userId:", error);
      }
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
}

.chat-area {
  background-color: #fff;
  padding: 20px;
  border-radius: 5px;
}

.message {
  margin: 10px 0;
  padding: 10px;
  border-radius: 15px;
  border: 1px solid #ccc;
  max-width: 60%;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.message.from-ai {
  align-self: flex-start;
  background-color: #e3f2fd;
  border-color: #bbdefb;
}

.message.from-user {
  align-self: flex-end;
  background-color: #c8e6c9;
  border-color: #a5d6a7;
}

textarea {
  width: 100%;
  height: 100px;
  resize: none;
  padding: 5px;
  box-sizing: border-box;
  font-size: 14px;
}

textarea:focus {
  outline: none;
}

button {
  margin-top: 10px;
  padding: 10px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}
</style>
