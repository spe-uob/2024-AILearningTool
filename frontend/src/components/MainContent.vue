<template>
  <main>
    <div class="chat-area">
      <p>Welcome to Watsonx AI!</p>
        <p>Select two topics below:</p>
      <button @click="startTopic('First Time Coming to University')">First Time Coming to University</button>
      <button @click="startTopic('Academic Inquiry')">Academic Inquiry</button>

      <!-- Display messages with smart wrapping -->
      <div v-for="msg in processedMessages" :key="msg.id" class="message">
        {{ msg.content }}
      </div>

      <!-- Input area for new messages -->
      <textarea
          v-model="userInput"
          @input="handleInput"
      ></textarea>


      <!-- Send message button-->
      <button @click="sendMessage">Send</button>
    </div>
  </main>
</template>

<script>
export default {
  data() {
    return {
      userInput: '',
      messages: [],
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
      // The function intelligently wraps text by finding the nearest space
      let result = '';
      let currentLine = '';
      const words = text.split(' '); // Split text into words by space

      words.forEach((word) => {
        if ((currentLine + word).length > maxLength) {
          result += currentLine.trim() + '\n'; // Add current line to result with a new line
          currentLine = word + ' '; // Start a new line with the current word
        } else {
          currentLine += word + ' '; // Add word to the current line
        }
      });

      // Add any remaining text to the result
      result += currentLine.trim();
      return result;
    },
    sendMessage() {
      if (this.userInput.trim()) {
        this.messages.push({ id: Date.now(), content: this.userInput });
        this.userInput = '';  // Clear input field after sending message
      }
    },
    handleInput() {
      // Any specific logic for handling input
    },
    startTopic(topic) {
      this.messages.push({ id: Date.now(), content: `Selected topic: ${topic}` });
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
  white-space: pre-wrap; /* Ensures that new lines are respected */
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
