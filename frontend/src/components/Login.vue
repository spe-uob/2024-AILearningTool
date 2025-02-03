<template>
    <div class="login-container">
      <h2>Login</h2>
      <form @submit.prevent="handleLogin">
        <div class="input-group">
          <label for="username">Username</label>
          <input v-model="username" id="username" type="text" required />
        </div>
        <div class="input-group">
          <label for="password">Password</label>
          <input v-model="password" id="password" type="password" required />
        </div>
        <button type="submit" :disabled="loading">{{ loading ? 'Logging in...' : 'Login' }}</button>
        <p v-if="error" class="error">{{ error }}</p>
      </form>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import axios from 'axios';
  
  const username = ref('');
  const password = ref('');
  const loading = ref(false);
  const error = ref(null);
  const router = useRouter();
  
  const handleLogin = async () => {
    loading.value = true;
    error.value = null;
  
    try {
      const response = await axios.post('http://localhost:8080/api/auth/login', {
        username: username.value,
        password: password.value,
      });
      
      localStorage.setItem('token', response.data.token);
      router.push('/dashboard');
    } catch (err) {
      error.value = 'Invalid username or password';
    } finally {
      loading.value = false;
    }
  };
  </script>
  
  <style scoped>
  .login-container {
    max-width: 300px;
    margin: 100px auto;
    padding: 20px;
    border: 1px solid #ccc;
    border-radius: 8px;
    box-shadow: 2px 2px 12px rgba(0, 0, 0, 0.1);
  }
  .input-group {
    margin-bottom: 15px;
  }
  label {
    display: block;
    margin-bottom: 5px;
  }
  input {
    width: 100%;
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
  }
  button {
    width: 100%;
    padding: 10px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  button:disabled {
    background-color: #aaa;
  }
  .error {
    color: red;
    margin-top: 10px;
  }
  </style>
  
