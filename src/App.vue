<template>
  <div id="app">
    <!-- Wrapper for colorblind mode -->
    <div :class="colorblindMode ? 'red-green-colorblind' : ''">
      <!-- routing view -->
      <router-view />
    </div>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      colorblindMode: false, // Track colorblind mode state
    };
  },
  methods: {
    onColorblindToggled(isColorblind) {
      this.colorblindMode = isColorblind; // Update colorblindMode based on event
    },
  },
};
</script>

<style>
/* Adding Global Styles */
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 0;
}

/* Apply colorblind mode when enabled */
.red-green-colorblind {
  --primary-color: #004b23; /* Dark Green */
  --secondary-color: #f2b104; /* Gold */
  --accent-color: #ef6c6c; /* Soft Red */
  --background-color: #f4f4f4;
  --text-color: #2e2e2e;
  --border-color: #d3d3d3;
  --button-color: #4caf50;
  --error-color: #e74c3c;
  --success-color: #27ae60;
}

/* Apply the color scheme globally */
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
</style>

