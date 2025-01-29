<template>
  <aside :class="{'disabled': isDisabled, 'collapsed': isCollapsed}" :style="asideStyles">
    <button :style="buttonStyles" @click="toggleSidebar">Important</button>
    <div v-if="!isCollapsed && !isDisabled">
      <h3 :style="textStyles">Memo</h3>
      <textarea
        v-model="memoContent"
        placeholder="Write your memo here"
        @input="handleInput"
        :style="textareaStyles"
      ></textarea>
    </div>
  </aside>
</template>

<script>
import { getTheme } from "../assets/color.js";

export default {
  props: {
    isDisabled: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      isCollapsed: false,
      memoContent: "",
      currentTheme: "default", // Tracks the current theme
      themeStyles: {}, // Stores styles for the theme
    };
  },
  methods: {
    toggleSidebar() {
      this.isCollapsed = !this.isCollapsed;
    },
    handleInput() {
      // Handle input if necessary
    },
    applyTheme(themeName) {
      const theme = getTheme(themeName);
      this.themeStyles = {
        aside: {
          backgroundColor: theme.background,
          color: theme.text,
          border: `1px solid ${theme.border}`,
        },
        button: {
          backgroundColor: theme.button,
          color: theme.text,
        },
        text: {
          color: theme.text,
        },
        textarea: {
          backgroundColor: theme.background,
          color: theme.text,
          border: `1px solid ${theme.border}`,
        },
      };
    },
    // Listen for theme change events
    listenForThemeChange() {
      window.addEventListener("themeChange", (event) => {
        this.applyTheme(event.detail.themeName);
      });
    },
  },
  computed: {
    asideStyles() {
      return this.themeStyles.aside;
    },
    buttonStyles() {
      return this.themeStyles.button;
    },
    textStyles() {
      return this.themeStyles.text;
    },
    textareaStyles() {
      return this.themeStyles.textarea;
    },
  },
  mounted() {
    // Apply the default theme when the component is mounted
    this.applyTheme("default");

    // Start listening for theme changes
    this.listenForThemeChange();
  },
};
</script>

<style scoped>
aside {
  width: 250px;
  padding: 10px;
  transition: width 0.3s ease;
}

.disabled {
  pointer-events: none;
  opacity: 0.5;
}

.collapsed {
  width: 50px;
}

textarea {
  width: 100%;
  height: 800px;
  resize: none;
  padding: 5px;
  box-sizing: border-box;
  font-size: 14px;
}

textarea:focus {
  outline: none;
}

button {
  margin: 5px 0;
  padding: 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  opacity: 0.9;
}

p {
  font-size: 12px;
  color: gray;
}
</style>
