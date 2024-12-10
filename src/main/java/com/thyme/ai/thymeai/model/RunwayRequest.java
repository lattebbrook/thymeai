package com.thyme.ai.thymeai.model;

public class RunwayRequest {

    private String promptImage; //base64
    private String promptText; // required
    private final String model = "gen3a_turbo";

    public RunwayRequest(String promptImage, String promptText) {
        this.promptImage = promptImage;
        this.promptText = promptText;
    }

    public RunwayRequest() {
    }

    public String getPromptImage() {
        return promptImage;
    }

    public void setPromptImage(String promptImage) {
        this.promptImage = promptImage;
    }

    public String getPromptText() {
        return promptText;
    }

    public void setPromptText(String promptText) {
        this.promptText = promptText;
    }

    public String getModel() {
        return model;
    }
}
