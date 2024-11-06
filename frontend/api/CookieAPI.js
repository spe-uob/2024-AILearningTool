// src/api/api.js
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';


const api = axios.create({
    baseURL: API_BASE_URL,
    withCredentials: true,
});


export function signupWithConsent() {
    return api.get('/signup');
}


export function revokeConsent() {
    return api.get('/revokeConsent');
}
