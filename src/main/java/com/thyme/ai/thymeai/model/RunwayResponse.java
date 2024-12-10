package com.thyme.ai.thymeai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RunwayResponse {

    private String id;
    private String status;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("output")
    private List<String> output;

}
