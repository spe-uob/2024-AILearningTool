<template>
  <div id="app">
    <!-- Wrapper for colorblind mode -->
    <div :class="colorblindMode ? 'red-green-colorblind' : ''">
      <!-- routing view -->
      <router-view />
    </div>

    <!-- Include Settings Sidebar -->
    <SettingSidebar @colorblindToggled="onColorblindToggled" />
  </div>
</template>

<script>
import SettingSidebar from './SettingSidebar.vue';

export default {
  name: 'App',
  components: {
    SettingSidebar,
  },
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
/* Apply colorblind mode globally */
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
