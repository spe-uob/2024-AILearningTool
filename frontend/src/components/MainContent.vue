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
          <!-- Display sender name and avatar -->
          <div class="message-header">
            <img :src="msg.avatarUrl" alt="Avatar" class="avatar">
            <span class="sender-name">{{ msg.senderName }}</span>
          </div>
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
        this.messages.push({
          id: Date.now(),
          content: this.userInput,
          senderName: 'User', // Add sender's name
          avatarUrl: 'https://via.placeholder.com/40' // Placeholder avatar URL
        });
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
        this.messages.push({ id: Date.now(), content: `Selected topic: ${topic}`, senderName: 'System', avatarUrl: 'https://via.placeholder.com/40' });
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
  padding: 10px;
  background-color: #e0f7fa;
  border-radius: 15px;
  border: 1px solid #b2ebf2;
  max-width: 60%;
  white-space: pre-wrap; /* Ensures that new lines are respected */
  word-wrap: break-word;
}

.message:nth-child(odd) {
  align-self: flex-start;
  background-color: #e3f2fd;
  border-color: #bbdefb;
}

.message:nth-child(even) {
  align-self: flex-end;
  background-color: #c8e6c9;
  border-color: #a5d6a7;
}

.message-header {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}

.avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  margin-right: 10px;
}

.sender-name {
  font-weight: bold;
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


