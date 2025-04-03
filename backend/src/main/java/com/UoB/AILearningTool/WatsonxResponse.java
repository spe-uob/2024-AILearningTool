package com.UoB.AILearningTool;

public class WatsonxResponse {
    public String responseText;
    public Integer statusCode;
    public WatsonxResponse(Integer statusCode, String responseText) {
        this.statusCode = statusCode;
        this.responseText = responseText;
    }
    public int status() {
        return statusCode;
    }

    public String message() {
        return responseText;
    }

}
