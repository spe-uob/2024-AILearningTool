package com.UoB.AILearningTool;

// StringTools create / transform strings to the required formats.
public class StringTools {

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
        String x = "{\"input\": \"" + input.replace("\n", "\\n")
                .replace("\'", "'\''")
                .replace("\"", "\\\"") + "\\n<|assistant|>\\n\",";

        System.out.println(x);
        return x;
    }
}
