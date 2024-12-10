package com.thyme.ai.thymeai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserData {

    private String Id;
    private boolean isAboveLimit;
    private String state;
    private String userImagePrompt;
    private String userTextPrompt;
    private String videoId;
    private String videoUrl;
}
