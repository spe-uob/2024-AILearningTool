<template>
  <div id="app">
    <!-- Wrapper for applying theme dynamically -->
    <div>
      <!-- routing view -->
      <router-view />
    </div>

    <!-- Include Settings Sidebar -->
    <SettingSidebar @colorblindToggled="onColorblindToggled" />
  </div>
</template>

<script>
import SettingSidebar from "./SettingSidebar.vue";
import { getTheme } from "@/color.js";

export default {
  name: "App",
  components: {
    SettingSidebar,
  },
  data() {
    return {
      currentTheme: "default", // Track the current theme
    };
  },
  methods: {
    onColorblindToggled(isColorblind) {
      // Set the theme based on colorblind mode state
      this.currentTheme = isColorblind ? "colorblind_red_green" : "default";
      this.applyTheme(this.currentTheme);
    },
    applyTheme(themeName) {
      // Apply the selected theme using CSS variables
      const theme = getTheme(themeName);
      Object.keys(theme).forEach((key) => {
        document.documentElement.style.setProperty(`--${key}-color`, theme[key]);
      });
    },
  },
  mounted() {
    // Apply the default theme when the app loads
    this.applyTheme("default");
  },
};
</script>

<style>
/* Apply the color scheme globally using CSS variables */
body {
  background-color: var(--background-color);
  color: var(--text-color);
}

button {
  background-color: var(--button-color);
  color: white;
}

.success {
  color: var(--success-color);
}

.error {
  color: var(--error-color);
}

.border {
  border-color: var(--border-color);
}

/* Additional styles for accessibility */
a {
  color: var(--accent-color);
}

header,
footer {
  background-color: var(--primary-color);
  color: var(--secondary-color);
}
</style>
