<template>
  <main>
    <div class="chat-area">
      <!-- Status display for speech recognition -->
      <div v-if="listeningStatusMessage" class="listening-status">
        {{ getTranslation(currentLanguage, listeningStatusMessage) }}
      </div>

      <!-- Welcome screen with logo and initial options -->
      <div v-if="currentChatID.length === 0" class="welcome-container">
        <img src="../assets/logo.png" alt="Logo" class="logo" />
        <p class="welcome-text">
          {{ getTranslation(currentLanguage, 'WELCOME_TO_WATSONX_AI') }}
        </p>
        <p class="instruction-text">
          {{ getTranslation(currentLanguage, 'SELECT_INITIAL_TOPIC') }}
        </p>
        <!-- Initial topic selection buttons -->
        <div class="button-container">
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

      <!-- Chat UI section -->
      <div v-if="currentChatID.length > 0" class="chat-container">
        <!-- Message display area -->
        <div class="messages-container" ref="messagesContainer">
          <div v-for="(msg, index) in messages" :key="index">
            <div class="message" :class="{
              'user-message': msg.sender === 'user',
              'assistant-message': msg.sender === 'assistant',
              'system-message': msg.sender === 'System'
            }">
              <strong v-if="msg.sender === 'user'">{{ getTranslation(currentLanguage, "USER") }}</strong>
              <strong v-else-if="msg.sender === 'assistant'">{{ getTranslation(currentLanguage, "AI") }}</strong>
              <strong v-else>{{ msg.sender }}</strong>
              <!-- Animated typing effect for assistant response -->
              <TypingText v-if="msg.sender === 'assistant'" :text="formatMessage(msg.content)" :speed="15" />
              <!-- Regular message rendering with markdown support -->
              <p v-else v-html="formatMessage(msg.content)"></p>
            </div>
            <!-- Text-to-Speech button for assistant message -->
            <div v-if="msg.sender === 'assistant'" class="tts-button-wrapper">
              <button @click="speakMessage(msg.content)" class="tts-button">
                <i class="fa fa-volume-up"></i>
              </button>
            </div>
          </div>
        </div>

        <!-- Input area with text and voice support -->
        <div class="input-area">
          <textarea
              v-model="userInput"
              :placeholder="getTranslation(currentLanguage, 'TYPE_YOUR_MESSAGE')"
              @keypress.enter.prevent="sendMessage"
          ></textarea>

          <!-- Speech recognition toggle button -->
          <button @click="toggleSpeechRecognition" :class="{ listening: isListening }">
            🎤 {{ isListening ? getTranslation(currentLanguage, 'STOP_VOICE_INPUT') : getTranslation(currentLanguage, 'START_VOICE_INPUT') }}
          </button>

          <!-- Submit message button -->
          <button @click="sendMessage" :disabled="chatInitButtonsDisabled">
            {{ getTranslation(currentLanguage, "SEND") }}
          </button>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
// Import necessary utilities and components
import { marked } from "marked";
import { getTheme } from "../assets/color.js";
import { getTranslation } from "../assets/language";
import { BACKEND_URL } from "@/assets/globalConstants";
import TypingText from "../components/helpers/TypingText.vue";

export default {
  components: { TypingText },
  data() {
    return {
      userInput: "",                     // Current user input text
      currentTopic: "",                  // Current topic (if any)
      currentTurn: "user",              // Current turn owner (user/assistant)
      userId: localStorage.getItem("userId") || "",
      currentTheme: "default",          // Theme setting
      recognition: null,                 // Web Speech API recognition instance
      isListening: false,                // Whether speech recognition is active
      listeningStatusMessage: ""        // Status message for recognition UI
    };
  },
  props: ["messages", "chats", "currentChatID", "currentLanguage", "chatInitButtonsDisabled"],
  watch: {
    // Dynamically update language for speech recognition
    currentLanguage(newLang) {
      if (this.recognition) {
        const langMap = {
          en: 'en-US',
          zh: 'zh-CN',
          ru: 'ru-RU'
        };
        this.recognition.lang = langMap[newLang] || 'en-US';
      }
    },
    // Scroll to bottom when new messages arrive
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
  mounted() {
    // Initialize speech recognition if supported
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (SpeechRecognition) {
      this.recognition = new SpeechRecognition();

      const langMap = {
        en: 'en-US',
        zh: 'zh-CN',
        ru: 'ru-RU'
      };

      this.recognition.lang = langMap[this.currentLanguage] || 'en-US';
      this.recognition.interimResults = false;
      this.recognition.continuous = true;

      // Handle successful speech recognition
      this.recognition.onresult = (event) => {
        const result = event.results[event.results.length - 1][0].transcript;
        this.userInput = result;
        this.listeningStatusMessage = "LISTENING";
      };

      // Handle recognition errors
      this.recognition.onerror = (event) => {
        console.error("Speech recognition error:", event.error);
        this.isListening = false;
        this.listeningStatusMessage = "VOICE_RECOGNITION_ERROR";
      };

      // Update listening state when recognition starts
      this.recognition.onstart = () => {
        this.isListening = true;
        this.listeningStatusMessage = "LISTENING";
      };

      // Restart recognition if listening was manually enabled
      this.recognition.onend = () => {
        if (this.isListening) {
          this.recognition.start();
        }
      };
    } else {
      this.listeningStatusMessage = "SPEECH_RECOGNITION_NOT_SUPPORTED";
    }
  },
  methods: {
    getTheme,
    getTranslation,

    // Parse markdown in message content
    formatMessage(message) {
      return marked(message);
    },

    // Speak assistant message using Web Speech API
    speakMessage(text) {
      if (window.speechSynthesis.speaking) {
        window.speechSynthesis.cancel();
        return;
      }
      const utterance = new SpeechSynthesisUtterance(text);
      utterance.rate = 1.0;
      utterance.pitch = 1.0;
      utterance.lang = this.currentLanguage || "en-US";

      const assignVoice = () => {
        const voices = window.speechSynthesis.getVoices();
        let preferredVoice;
        if (utterance.lang.startsWith("en")) {
          preferredVoice = voices.find(
            (v) => v.lang === "en-GB" && v.name === "Google UK English Female"
          ) || voices.find((v) => v.lang === "en-GB") || voices.find((v) => v.lang === "en-US");
        } else {
          preferredVoice = voices.find((v) => v.lang === utterance.lang);
        }
        if (preferredVoice) {
          utterance.voice = preferredVoice;
        }
        window.speechSynthesis.speak(utterance);
      };

      const voices = window.speechSynthesis.getVoices();
      if (voices.length === 0) {
        window.speechSynthesis.onvoiceschanged = assignVoice;
      } else {
        assignVoice();
      }
    },

    // Toggle speech recognition on/off
    toggleSpeechRecognition() {
      if (!this.recognition) {
        this.listeningStatusMessage = "SPEECH_RECOGNITION_NOT_SUPPORTED";
        alert(this.getTranslation(this.currentLanguage, 'SPEECH_RECOGNITION_NOT_SUPPORTED'));
        return;
      }
      if (this.isListening) {
        this.recognition.stop();
        this.isListening = false;
        this.listeningStatusMessage = "";
      } else {
        const langMap = {
          en: 'en-US',
          zh: 'zh-CN',
          ru: 'ru-RU'
        };
        this.recognition.lang = langMap[this.currentLanguage] || 'en-US';
        this.recognition.start();
        this.isListening = true;
        this.listeningStatusMessage = "LISTENING";
      }
    },

    // Start new conversation with predefined question
    async sendInitialMessage(message) {
      this.$emit("setButtonLock", true);
      let response = await fetch(BACKEND_URL + "/createChat?" + new URLSearchParams({ initialMessage: message }), {
        method: "GET",
        credentials: "include",
      });

      if (!response.ok) throw new Error("Non-200 backend API response");

      this.$emit("updateChatID", await response.text());
      await this.$nextTick(() => {
        this.$emit("addChat", this.currentChatID, message);
        this.requestChatHistory();
      });
    },

    // Request full chat history from backend
    requestChatHistory() {
      fetch(BACKEND_URL + "/getChatHistory?" + new URLSearchParams({ chatID: this.currentChatID }), {
        method: "GET",
        credentials: "include",
      }).then(async (response) => {
        if (!response.ok) throw new Error("Non-200 backend API response");
        await this.processChatHistory(await response.json());
      });
    },

    // Reconstruct local chat history from server
    async processChatHistory(messageHistory) {
      for (let i = 0; i < messageHistory.length; i++) {
        this.$emit("addMessage", ((i % 2 === 0) ? "user" : "assistant"), messageHistory[i]["content"]);
      }
      this.$emit("setButtonLock", false);
    },

    // Send user message and receive assistant reply
    async sendMessage() {
      if (!this.userInput.trim()) {
        alert(getTranslation(localStorage.getItem("langCode"), "PLEASE_ENTER_A_MESSAGE"));
        return;
      }
      if (!this.currentChatID) {
        alert("ChatId is not set. Please start a new chat.");
        return;
      }
      this.$emit("addMessage", "user", this.userInput);
      this.$emit("setButtonLock", true);

      const messageToSend = this.userInput.trim();
      this.userInput = "";

      try {
        let response = await fetch(BACKEND_URL + "/sendMessage?" + new URLSearchParams({
          userID: this.userId,
          chatID: this.currentChatID,
          newMessage: messageToSend,
        }), { method: "GET", credentials: "include" });

        if (!response.ok) throw new Error(`Unexpected response code: ${response.status}`);

        let responseJSON = await response.json();
        this.$emit("addMessage", "assistant", (responseJSON["content"]));
      } catch (error) {
        console.error("Error sending message:", error);
        this.$emit("addMessage", "System", localStorage.getItem("langCode"), "FAILED_TO_SEND_MESSAGE");
      }

      this.scrollToBottom();
      this.$emit("setButtonLock", false);
    },

    // Auto scroll to bottom when messages update
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesContainer;
        if (container) container.scrollTo({ top: container.scrollHeight, behavior: "smooth" });
      });
    }
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

/* Add download button style */
.download-container {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 10;
}

.download-btn {
  padding: 8px;
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 20px;
  border-radius: 50%;
  transition: background-color 0.3s;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.download-btn:hover {
  background-color: var(--border-color);
}

/* Ensure links in messages are visible */
.messages-container a {
  color: blue;
  text-decoration: underline;
}

.listening {
  animation: pulse 1s infinite;
  border: 2px solid var(--accent-color);
  box-shadow: 0 0 0 4px rgba(0, 123, 255, 0.3);
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.015); }
  100% { transform: scale(1); }
}

.listening-status {
  text-align: center;
  font-weight: bold;
  font-size: 1.1em;
  color: var(--accent-color);
  padding: 10px;
}
</style>
