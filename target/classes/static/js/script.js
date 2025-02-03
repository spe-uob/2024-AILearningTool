let optionalConsent = false;
let currentTurn = "user"
const messageContainer = document.getElementById("messageContainer")

// Creates "optionalConsent" cookie
function setConsentCookie() {
    const d = new Date(1000*60*60*24*30);
    d.setTime(d.getTime() + (24*30));
    let expires = "expires="+ d.toUTCString();
    document.cookie = "optionalConsent=" + optionalConsent + ";" + expires + ";path=/";
}

// Sends signup GET request
function signUp() {
    fetch("http://localhost:8080/signup",
        {
            method: "GET",
            credentials: "include",
        }
    ).then(response => {
        if (!response.ok) {
            throw new Error("Non-200 backend API response");
        } else {
            document.getElementById("cookiePopUp").style.display = "none"
        }
    })
}

// Adds a message to the UI.
function addMessageToUI(message) {
    messageContainer.innerHTML += ("<hr>\n" +
        "            <div class=\"" + currentTurn + "Message\">\n" +
        "                <div>" + currentTurn + "</div>\n" +
        "                <div>" + message + "</div>\n" +
        "            </div>")
    if (currentTurn === "user") {
        currentTurn = "assistant"
    } else {
        currentTurn = "user"
    }
}

// Receives cookie preference from user and signs up in the system.
function acceptOpt(x) {
    optionalConsent = x
    setConsentCookie()
    signUp()
}

// Parses chat history from HTTP response to good UI.
function processChatHistory(messageHistory) {
    messageHistory = String(messageHistory)
    let currentRole;
    let openAIRole;
    let nextRole;
    let nextRoleIndex;
    let currentMessage;
    for (let i = 0; ; i++) {
        if (i === 0) {
            // Parameters for parsing first message in chat history (system prompt)
            currentRole = "<|system|>";
            openAIRole = "system";
            nextRole = "<|user|>";
        } else if (i % 2 !== 0) {
            // Parameters for parsing a user message
            currentRole = "<|user|>";
            openAIRole = "user";
            nextRole = "<|assistant|>";
        } else {
            // Parameters for parsing an AI message
            currentRole = "<|assistant|>";
            openAIRole = "assistant";
            nextRole = "<|user|>";
        }
        if (messageHistory.includes(currentRole)) {
            // Removing previous messages
            messageHistory = messageHistory.substring(messageHistory.indexOf(currentRole) + currentRole.length);
            // Parsing a message
            nextRoleIndex = messageHistory.indexOf(nextRole);
            if (nextRoleIndex !== -1) {
                currentMessage = messageHistory.substring(0, messageHistory.indexOf(nextRole));
            } else {
                currentMessage = messageHistory;
            }
            if (currentRole === "<|system|>") {continue}
            addMessageToUI(currentMessage)
        } else {
            break;
        }
    }
}

// Create a new chat and requests its history
function createChat(firstChoice) {
    fetch("http://localhost:8080/createChat?" + new URLSearchParams({
        "initialMessage": firstChoice
    }),
        {
            method: "GET",
            credentials: "include",
        }
    ).then(async response => {
        if (!response.ok) {
            throw new Error("Non-200 backend API response");
        }
        localStorage.setItem("chatID", await response.text())
        getChatHistory()
    })
    document.getElementById("chatInitialiser").setAttribute("display", "none")
}

// Makes a request for chat history
function getChatHistory() {
    fetch("http://localhost:8080/getChatHistory?" + new URLSearchParams({
        "chatID": localStorage.getItem("chatID")
    }),
        {
            method: "GET",
            credentials: "include",
        }
    ).then(async response => {
        if (!response.ok) {
            throw new Error("Non-200 backend API response");
        } else {
            processChatHistory(await response.text())
        }

    })
    document.getElementById("chatInitialiser").style.display = "none"
}

// Sends a message to existing chat
function sendMessage() {
    addMessageToUI(document.getElementById("promptInput").value);
    fetch("http://localhost:8080/sendMessage?" + new URLSearchParams({
        "chatID": localStorage.getItem("chatID"),
        "newMessage": document.getElementById("promptInput").value
    }),
        {
            method: "GET",
            credentials: "include",
        }
    ).then(async response => {
        if (!response.ok) {
            throw new Error("Non-200 backend API response");
        } else {
            addMessageToUI(await response.text())
        }
    })
}

function revokeConsent() {
  fetch("http://localhost:8080/revokeConsent",
      {
        method: "GET",
        credentials: "include",
      }
  ).then(response => {
    if (!response.ok) {
      throw new Error("Non-200 backend API response");
    }
  })
}

