<template>
  <main>
    <div class="chat-area">
      <!-- Welcome Screen with Logo -->
      <div v-if="currentChatID.length === 0" class="welcome-container">
        <img src="../assets/logo.png" alt="Logo" class="logo" />
        <p class="welcome-text">
          {{ getTranslation(currentLanguage, 'WELCOME_TO_WATSONX_AI') }}
        </p>
        <p v-if="currentChatID.length === 0" class="instruction-text">
          {{ getTranslation(currentLanguage, 'SELECT_INITIAL_TOPIC') }}
        </p>

        <!-- Buttons for chat initialisation -->
        <div v-if="currentChatID.length === 0" class="button-container">
          <button 
            @click="sendInitialMessage(getTranslation(currentLanguage, 'I_NEED_HELP_WITH_CHOOSING_A_COURSE'))" 
            :disabled="chatInitButtonsDisabled">
            {{ getTranslation(currentLanguage, "I_NEED_HELP_WITH_CHOOSING_A_COURSE") }}
          </button>
          <button 
            @click="sendInitialMessage(getTranslation(currentLanguage, 'I_NEED_HELP_WITH_PLATFORM'))" 
            :disabled="chatInitButtonsDisabled">
            {{ getTranslation(currentLanguage, "I_NEED_HELP_WITH_PLATFORM") }}
          </button>
          <button 
            @click="sendInitialMessage(getTranslation(currentLanguage, 'I_HAVE_QUESTIONS_ABOUT_UNI_LIFE'))" 
            :disabled="chatInitButtonsDisabled">
            {{ getTranslation(currentLanguage, "I_HAVE_QUESTIONS_ABOUT_UNI_LIFE") }}
          </button>
        </div>
      </div>

      <!-- Display all messages in the conversation -->
      <div v-if="currentChatID.length > 0" class="chat-container">
        <!-- Scrollable message area -->
        <div class="messages-container" ref="messagesContainer">
          <div v-for="(msg, index) in messages" :key="index">
            <div 
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
              <!-- For assistant messages, use TypingText to animate the output -->
              <TypingText 
                v-if="msg.sender === 'assistant'" 
                :text="formatMessage(msg.content)" 
                :speed="15" 
              />
              <!-- For non-assistant messages, render markdown as HTML -->
              <p v-else v-html="formatMessage(msg.content)"></p>
            </div>
            <!-- TTS Button: For assistant messages, show a speaker icon below the message -->
            <div v-if="msg.sender === 'assistant'" class="tts-button-wrapper">
              <button @click="speakMessage(msg.content)" class="tts-button">
                <i class="fa fa-volume-up"></i>
              </button>
            </div>
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
import { marked } from "marked";
import { getTheme } from "../assets/color.js";
import {getTranslation} from "../assets/language";
import TypingText from "../components/helpers/TypingText.vue";

export default {
  components: {
    TypingText,
  },
  data() {
    return {
      userInput: "",
      userId: localStorage.getItem("username") || "",  // username
      aiServerURL: "http://localhost:8080", // API address
      sessionID: localStorage.getItem("sessionID") || "",
    };
  },
  props: ["messages", "chats", "currentChatID", "currentLanguage", "chatInitButtonsDisabled"],
  watch: {
    messages() {
      if (this.messages.length === 0 && this.currentChatID.length > 0) {
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

    updateChatList(newChats) {
      this.chats = newChats;
      this.$emit('updateChats', newChats);
    },

    formatMessage(message) {
      return marked(message);
    },

    resetContent() {
      this.messages = [];
      this.$emit('updateChatID', '');
    },


        /**
     * Uses the Web Speech API to speak the given text.
     */
    speakMessage(text) {
      // If something is currently being spoken, cancel it and return.
      if (window.speechSynthesis.speaking) {
        window.speechSynthesis.cancel();
        return;
      }

      // Create a new utterance with the provided text.
      const utterance = new SpeechSynthesisUtterance(text);
      // Set the rate and pitch.
      utterance.rate = 1.0;
      utterance.pitch = 1.0;
      // Set the language from currentLanguage (or default to 'en-US').
      utterance.lang = this.currentLanguage || "en-US";

      // Function to select and assign a voice, then speak the utterance.
      const assignVoice = () => {
        const voices = window.speechSynthesis.getVoices();
        let preferredVoice;
        // If the language is English, try to select the preferred English voice.
        if (utterance.lang.startsWith("en")) {
          preferredVoice = voices.find(
            (v) => v.lang === "en-GB" && v.name === "Google UK English Female"
          ) || voices.find((v) => v.lang === "en-GB") || voices.find((v) => v.lang === "en-US");
        } else {
          // Otherwise, select a voice that matches the utterance language.
          preferredVoice = voices.find((v) => v.lang === utterance.lang);
        }
        if (preferredVoice) {
          utterance.voice = preferredVoice;
        }
        window.speechSynthesis.speak(utterance);
      };

      // Get the voices list. If it's empty, wait for voices to be loaded.
      const voices = window.speechSynthesis.getVoices();
      if (voices.length === 0) {
        window.speechSynthesis.onvoiceschanged = assignVoice;
      } else {
        assignVoice();
      }
    },
    
    /**
     * Initializes a new chat with a predefined message.
     */
    async sendInitialMessage(message) {
      if (!this.sessionID) {
        alert("Please login!");
        return;
      }

      try {
        const chatCount = this.chats.length + 1;
        const chatTitle = `Chat ${chatCount}`;

        const response = await fetch(`${this.aiServerURL}/createChat`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            sessionID: this.sessionID,
            initialMessage: message
          })
        });

        const data = await response.json();

        if (!response.ok) {
          throw new Error(data.message || "Failed to create chat");
        }

        this.$emit("updateChatID", data.chatID);
        this.$emit("addChat", data.chatID, chatTitle);
        this.$emit("addMessage", "user", message);

        if (data.aiResponse) {
          this.$emit("addMessage", "assistant", data.aiResponse);
        }

      } catch (error) {
        console.error("Error creating chat:", error);
        alert(error.message || "Failed to create chat");
      }
    },


    async sendMessage() {
      if (!this.userInput.trim()) {
        alert("Please enter messages");
        return;
      }

      if (!this.userId) {
        alert("Login failed, please log in again!");
        return;
      }

      if (!this.currentChatID) {
        alert("Please select a chat first!");
        return;
      }

      const messageContent = this.userInput.trim();
      this.userInput = "";

      try {
        this.$emit("addMessage", "user", messageContent);

        const response = await fetch(`${this.aiServerURL}/sendMessage`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            username: this.userId,
            chatID: this.currentChatID,
            newMessage: messageContent,
          }),
        });

        const data = await response.json();

        if (!response.ok) {
          throw new Error(data.message || "Failed to send message");
        }

        this.$emit("addMessage", "assistant", data.response);

      } catch (error) {
        console.error("Error sending message:", error);
        alert(error.message || "Failed to send message");
      }
    },


    async requestChatHistory() {
      if (!this.userId || !this.currentChatID) return;

      try {
        const response = await fetch(`${this.aiServerURL}/getChatHistory?` + new URLSearchParams({
          username: this.userId,
          chatID: this.currentChatID
        }), {
          method: "GET",
          credentials: "include",
        });

        if (!response.ok) throw new Error("Failed to load chat history");

        const data = await response.json();
        this.processChatHistory(data.history);
      } catch (error) {
        console.error("Error loading chat history:", error);
      }
    },

    /**
     * Processes retrieved chat history and structures it for display.
     */
    async processChatHistory(messageHistory) {
      if (!Array.isArray(messageHistory)) {
        console.error("Invalid chat history format:", messageHistory);
        return;
      }

      messageHistory.forEach(msg => {
        if (msg.content.startsWith("<|user|>")) {
          this.$emit("addMessage", "user", msg.content.replace("<|user|>", "").trim());
        } else if (msg.content.startsWith("<|assistant|>")) {
          this.$emit("addMessage", "assistant", msg.content.replace("<|assistant|>", "").trim());
        } else {
          this.$emit("addMessage", "system", msg.content);
        }
      });

      // 解锁按钮，确保 UI 流畅
      this.$emit("setButtonLock", false);
    },
    
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesContainer;
        if (container) {
          container.scrollTo({ top: container.scrollHeight, behavior: "smooth" });
        }
      });
    }
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

.tts-button-wrapper {
  margin-top: 5px;
  text-align: left;
}

.tts-button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.2em;
  color: var(--accent-color);
  padding: 2px;
}

.tts-button:hover {
color: var(--accent-color);
}
</style>
