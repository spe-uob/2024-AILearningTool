import axios from 'axios';

const API_BASE_URL = 'https://ailearningtool.ddns.net:8080';

const api = axios.create({
    baseURL: API_BASE_URL,
    withCredentials: true, // Make sure the cookie is included in the request
});

// Send a request for user consent status to /signup
export function signupWithConsent() {
    return api.get('/signup'); // Automatically carry `optionalConsent` cookie in request
}

// Send a request to revoke user consent status to /revokeConsent
export function revokeConsent() {
    return api.get('/revokeConsent'); // Automatically carry `optionalConsent` cookie in request
}

// Used to get the user's dialogue history
export function fetchUserConversation(userId) {
    return api.get('/conversation', { params: { userId } });
}
