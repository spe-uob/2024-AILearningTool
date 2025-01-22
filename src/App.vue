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
