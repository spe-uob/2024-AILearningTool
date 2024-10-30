<template>
  <main>
    <div class="chat-area">
      <p>Welcome to Watsonx AI!</p>
      <p v-if="!topicSelected">Select one of the topics below:</p>

      <!-- Buttons for selecting topics, only visible when no topic is selected -->
      <div v-if="!topicSelected">
        <button @click="startTopic('First Time Coming to University')">First Time Coming to University</button>
        <button @click="startTopic('Academic Inquiry')">Academic Inquiry</button>
      </div>

      <!-- Chatbox, visible only when a topic is selected -->
      <div v-if="chatboxVisible">
        <!-- Display messages with smart wrapping -->
        <div v-for="msg in processedMessages" :key="msg.id" class="message">
          {{ msg.content }}
        </div>

        <!-- Input area for new messages -->
        <textarea
            v-model="userInput"
            @input="handleInput"
            placeholder="Type your message..."
        ></textarea>

        <!-- Send message button-->
        <button @click="sendMessage">Send</button>
      </div>
    </div>
  </main>
</template>

<script>
export default {
  data() {
    return {
      userInput: '',
      messages: [],
      chatboxVisible: false,  // Tracks if the chatbox should be displayed
      topicSelected: false,   // Tracks if a topic has been selected
    };
  },
  computed: {
    processedMessages() {
      return this.messages.map(msg => {
        return {
          ...msg,
          content: this.wrapText(msg.content, 200),
        };
      });
    },
  },
  methods: {
    wrapText(text, maxLength) {
      let result = '';
      let currentLine = '';
      const words = text.split(' ');

      words.forEach((word) => {
        if ((currentLine + word).length > maxLength) {
          result += currentLine.trim() + '\n';
          currentLine = word + ' ';
        } else {
          currentLine += word + ' ';
        }
      });

      result += currentLine.trim();
      return result;
    },
    sendMessage() {
      if (this.userInput.trim()) {
        this.messages.push({ id: Date.now(), content: this.userInput });
        this.userInput = '';
      }
    },
    handleInput() {
      // Any specific logic for handling input
    },
    startTopic(topic) {
      // Only enable chatbox and hide buttons if a specific topic is selected
      if (topic === 'First Time Coming to University' || topic === 'Academic Inquiry') {
        this.chatboxVisible = true;
        this.topicSelected = true;
        this.messages.push({ id: Date.now(), content: `Selected topic: ${topic}` });
      }
    },
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
  white-space: pre-wrap;
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

p {
  font-size: 12px;
  color: gray;
}

button {
  margin-top: 10px;
  padding: 10px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:disabled {
  background-color: #ccc;
}
</style>
