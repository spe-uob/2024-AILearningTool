<template>
  <div id="app">
    <div>
      <router-view />
    </div>

    <SettingSidebar v-if="$route.path !== '/login'" @highContrastToggled="onHighContrastToggled" />
  </div>
</template>

<script>
import SettingSidebar from "./components/SettingSidebar.vue";
import { getTheme } from "./assets/color.js";

export default {
  name: "App",
  components: {
    SettingSidebar,
  },
  data() {
    return {
      currentTheme: "default",
    };
  },
  methods: {
    onHighContrastToggled(isHighContrast) {
      this.currentTheme = isHighContrast ? "high_contrast" : "default";
      this.applyTheme(this.currentTheme);
    },
    applyTheme(themeName) {
      const theme = getTheme(themeName);
      Object.keys(theme).forEach((key) => {
        document.documentElement.style.setProperty(`--${key}-color`, theme[key]);
      });
    },
  },
  mounted() {
    this.applyTheme("default");
  },
};
</script>

<style>
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

a {
  color: var(--accent-color);
}
header,
footer {
  background-color: var(--primary-color);
  color: var(--secondary-color);
}
</style>
