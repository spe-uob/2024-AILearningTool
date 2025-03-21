package com.UoB.AILearningTool;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

// StringTools create / transform strings to the required formats.
public class StringTools {

    public static String generateSessionID() {
        return RandomString(15);
    }

    // Generates a random string of specific size (e.g. for userID / chatID)
    public static String RandomString(int n) {
        String newString = "";
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        for (int i = 0; i < n; i++) {
            int index = (int)(characters.length() * Math.random());
            newString = newString + characters.charAt(index);
        }
        return newString;
    }

    // Prepares "input" JSON key value string for Watsonx API request
    public static String messageHistoryPrepare(String input) {
        input = input + "\n<|assistant|>\n";
        String x = "{\"input\": \"" + input.replace("\n", "\\n")
                .replace("\'", "'\''")
                .replace("\"", "\\\"") + "\",";

        System.out.println(x);
        return x;
    }

    // Convert Watsonx message history to OpenAI request payload
    public static String watsonxToOpenAI(String messageHistory) {
        String currentRole, nextRole, openAIRole;
        String currentMessage;
        JSONObject currentMessageJSON;
        JSONObject dataPayload = new JSONObject();
        dataPayload.put("model", "gpt-4o-mini");

        JSONArray messageArray = new JSONArray();

        // Parse the message history string.
        for (int i = 0; ;i++) {
            if (i == 0) {
                // Parameters for parsing first message in chat history (system prompt)
                currentRole = "<|system|>";
                openAIRole = "system";
                nextRole = "<|user|>";
            } else if (i % 2 != 0) {
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
            if (messageHistory.contains(currentRole)) {
                // Removing previous messages
                messageHistory = messageHistory.substring(messageHistory.indexOf(currentRole) + currentRole.length());
                // Parsing a message
                int nextRoleIndex = messageHistory.indexOf(nextRole);
                if (nextRoleIndex != -1) {
                    currentMessage = messageHistory.substring(0, messageHistory.indexOf(nextRole));
                } else {
                    currentMessage = messageHistory;
                }
                currentMessageJSON = new JSONObject();
                currentMessageJSON.put("role", openAIRole);
                currentMessageJSON.put("content", currentMessage);
                messageArray.put(currentMessageJSON);
            } else {
                break;
            }
        }

        dataPayload.put("messages", messageArray);
        return dataPayload.toString();
    }
}
