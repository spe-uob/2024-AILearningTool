<template>
    <!-- v-html so that if the formatted text contains HTML, it is rendered correctly -->
    <p v-html="displayedText"></p>
  </template>
  
  <script>
  export default {
    name: 'TypingText',
    props: {
      text: {
        type: String,
        required: true
      },
      speed: {
        type: Number,
        default: 15 // Delay in milliseconds between characters
      }
    },
    data() {
      return {
        displayedText: '',
        timer: null
      };
    },
    watch: {
      text(newText) {
        this.startTyping();
      }
    },
    mounted() {
      this.startTyping();
    },
    methods: {
      startTyping() {
        // Clear any existing timer and reset displayedText
        if (this.timer) {
          clearInterval(this.timer);
        }
        this.displayedText = '';
        let index = 0;
        this.timer = setInterval(() => {
          if (index < this.text.length) {
            this.displayedText += this.text[index];
            index++;
          } else {
            clearInterval(this.timer);
            this.$emit('finished');
          }
        }, this.speed);
      }
    },
    beforeDestroy() {
      if (this.timer) {
        clearInterval(this.timer);
      }
    }
  };
  </script>
  
  <style scoped>
  p {
    margin: 0;
    white-space: pre-wrap;
  }
  </style>
