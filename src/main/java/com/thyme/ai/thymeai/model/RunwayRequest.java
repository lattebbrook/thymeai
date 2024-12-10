package com.thyme.ai.thymeai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunwayRequest {

    private String promptImage; //base64
    private String promptText; // required
    private final String model = "gen3a_turbo";
    private final boolean watermark = false;
    private final String duration = "10";
    private final String ratio = "1280:768";

}
