<template>
  <main>
    <div class="chat-area">
      <p>Welcome to Watsonx AI!</p>

      <p v-if="this.currentChatID.length === 0">Select one of the topics below:</p>

      <!-- Buttons for chat initialisation -->
      <div v-if="this.currentChatID.length === 0">
        <button @click="sendInitialMessage('First Time Coming to University')">
          First Time Coming to University
        </button>
        <button @click="sendInitialMessage('Academic Inquiry')">Academic Inquiry</button>
      </div>

      <!-- All messages of the conversation -->
      <div v-if="this.currentChatID.length > 0">
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
      currentTopic: "",
      currentTurn: "user", // Used for message history parsing
      userId: localStorage.getItem("userId") || "",
      aiServerURL: "http://localhost:8080",
    };
  },
  props: ["messages", "chats", "currentChatID"],
  watch: {
    // When amount of messages = 0 and an existing chat has been chosen, chat history is loaded in MainContent
    messages() {
      if ((this.messages.length === 0) && (this.currentChatID.length > 0)) {
        this.currentTurn = "user"
        this.requestChatHistory()
      }
    }
  },
  methods: {
    // Send the first message, create a chat
    async sendInitialMessage(message) {
      let response = await fetch(this.aiServerURL + "/createChat?" + new URLSearchParams({
        "initialMessage": message
      }),{
            method: "GET",
            credentials: "include",
      });

      if (!response.ok) {
        throw new Error("Non-200 backend API response");
      }

      this.$emit("updateChatID", await response.text())
      await this.$nextTick(() => {
        this.$emit("addChat", this.currentChatID, message)
        this.requestChatHistory()
      })
    },


    // Make "getChatHistory" request for some chatID
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

    async processChatHistory(messageHistory) {
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
          this.$emit("addMessage", this.currentTurn, currentMessage)
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

    // Send a message in existing chat
    async sendMessage() {
      if (!this.userInput.trim()) {
        alert("Please enter a message!");
        return;
      }

      if (!this.currentChatID) {
        alert("ChatId is not set. Please start a new chat.");
        return;
      }

      this.$emit("addMessage", "user", this.userInput)

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

        this.$emit("addMessage", "assistant", response.data)
      } catch (error) {
        console.error("Error sending message:", error);

        this.$emit("addMessage", "System", "Failed to send message. Please try again.")
      }
    },
  },

    // TODO: Why is this here? We have Cookie.vue, right?
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
