<template>
  <main>
    <div class="chat-area">
      <p>Welcome to Watsonx AI!</p>
      <p v-if="!topicSelected">Select one of the topics below:</p>

      <!-- 话题选择按钮 -->
      <div v-if="!topicSelected">
        <button @click="startTopic('First Time Coming to University')">
          First Time Coming to University
        </button>
        <button @click="startTopic('Academic Inquiry')">Academic Inquiry</button>
      </div>

      <!-- 聊天窗口 -->
      <div v-if="topicSelected">
        <!-- 显示聊天消息 -->
        <div
            v-for="msg in messages"
            :key="msg.id"
            class="message"
            :class="{ 'from-ai': msg.sender === 'AI', 'from-user': msg.sender === 'You' }"
        >
          <strong v-if="msg.sender === 'AI'">AI:</strong>
          <strong v-else>You:</strong>
          <p>{{ msg.content }}</p>
        </div>

        <!-- 输入框 -->
        <textarea
            v-model="userInput"
            placeholder="Type your message..."
            @keypress.enter.prevent="sendMessage"
        ></textarea>

        <!-- 发送按钮 -->
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
      userInput: "", // 用户输入内容
      messages: [], // 聊天记录
      topicSelected: false, // 是否已选择话题
      currentTopic: "", // 当前话题
      userId: localStorage.getItem("userId") || "", // 存储用户 ID
      chatId: localStorage.getItem("chatId") || "", // 存储对话 ID
      aiServerUrl: "http://localhost:8080/api", // API 基础路径
    };
  },
  methods: {
    // 获取 userId（避免刷新后丢失）
    async getUserId() {
      try {
        const response = await axios.get(`${this.aiServerUrl}/signup`, { withCredentials: true });
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

    // 选择话题并启动对话
    async startTopic(topic) {
      this.topicSelected = true;
      this.currentTopic = topic;

      // 记录用户选择的主题
      this.messages.push({
        id: `system-${Date.now()}`,
        sender: "System",
        content: `You have selected the topic: ${topic}`,
      });

      try {
        // 创建新聊天会话
        const response = await axios.get(`${this.aiServerUrl}/createChat`, {
          params: { initialMessage: topic },
          withCredentials: true,
        });

        if (response.status !== 200) {
          throw new Error(`Unexpected response code: ${response.status}`);
        }

        // 存储 chatId
        this.chatId = response.data;
        localStorage.setItem("chatId", this.chatId);

        // 获取聊天历史记录
        await this.loadChatHistory();
      } catch (error) {
        console.error("Error starting chat:", error);
        this.messages.push({
          id: `error-${Date.now()}`,
          sender: "System",
          content: "Failed to start the chat. Please try again.",
        });
        this.topicSelected = false;
      }
    },

    // 发送消息
    async sendMessage() {
      if (!this.userInput.trim()) {
        alert("Please enter a message!");
        return;
      }

      if (!this.chatId) {
        alert("ChatId is not set. Please start a new chat.");
        return;
      }

      // 添加用户消息到聊天记录
      const userMessage = {
        id: `user-${Date.now()}`,
        sender: "You",
        content: this.userInput,
      };
      this.messages.push(userMessage);

      const messageToSend = this.userInput.trim();
      this.userInput = ""; // 清空输入框

      try {
        // 发送消息到后端
        const response = await axios.get(`${this.aiServerUrl}/sendMessage`, {
          params: {
            userId: this.userId,
            chatId: this.chatId,
            newMessage: messageToSend,
          },
          withCredentials: true,
        });

        if (response.status !== 200) {
          throw new Error(`Unexpected response code: ${response.status}`);
        }

        // 添加 AI 回复到聊天记录
        this.messages.push({
          id: `ai-${Date.now()}`,
          sender: "AI",
          content: response.data || "No response from AI.",
        });
      } catch (error) {
        console.error("Error sending message:", error);

        // 添加错误提示到聊天记录
        this.messages.push({
          id: `error-${Date.now()}`,
          sender: "System",
          content: "Failed to send message. Please try again.",
        });
      }
    },

    // 加载聊天历史记录
    async loadChatHistory() {
      if (!this.chatId) {
        console.error("No chatId found. Unable to load chat history.");
        return;
      }

      try {
        const response = await axios.get(`${this.aiServerUrl}/getChatHistory`, {
          params: { chatID: this.chatId },
          withCredentials: true,
        });

        if (response.status !== 200) {
          throw new Error("Failed to load chat history.");
        }

        // 解析历史消息
        this.messages = this.processChatHistory(response.data);
      } catch (error) {
        console.error("Error loading chat history:", error);
      }
    },

    // 解析聊天记录，去掉 `<|system|>` 标记
    processChatHistory(messageHistory) {
      const messages = [];
      const lines = messageHistory.split("\n");

      for (const line of lines) {
        if (line.includes("<|system|>") || line.includes("<|assistant|>")) {
          continue;
        }
        if (line.includes("<|user|>")) {
          messages.push({ sender: "You", content: line.replace("<|user|>", "").trim() });
        } else {
          messages.push({ sender: "AI", content: line.trim() });
        }
      }

      return messages;
    },
  },

  async mounted() {
    if (!this.userId) {
      await this.getUserId();
    }
    if (this.chatId) {
      this.loadChatHistory();
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
