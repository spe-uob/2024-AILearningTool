<template>
  <aside :class="{'disabled': isDisabled}">
    <button @click="toggleSidebar">Important</button>
    <div v-if="!isCollapsed && !isDisabled">
      <h3>Memo</h3>
      <textarea
          v-model="memoContent"
          placeholder="Write your memo here "
          @input="handleInput"
      ></textarea>
    </div>
  </aside>
</template>

<script>
export default
{
    props: {
     isDisabled: {
        type: Boolean,
        default: false,
     },
   },
    data() {
      return {
        isCollapsed: false,
        memoContent: '',
     };
    },
   methods: {
     toggleSidebar() {
       this.isCollapsed = !this.isCollapsed;
      },
      handleInput() {
       // Handle input if necessary
     },
    },
};
</script>

<style scoped>
aside {
  width: 250px;
  background-color: #f0f0f0;
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
}

p {
  font-size: 12px;
  color: gray;
}
</style>
