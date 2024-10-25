package com.UoB.AILearningTool;

public class RandomString {
    public static String make(int n) {
        String newString = "";
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        for (int i = 0; i < n; i++) {
            int index = (int)(characters.length() * Math.random());
            newString = newString + characters.charAt(index);
        }
        return newString;
    }
}
