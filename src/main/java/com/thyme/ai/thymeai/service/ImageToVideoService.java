package com.thyme.ai.thymeai.service;

import com.thyme.ai.thymeai.model.RunwayRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ImageToVideoService {

    private static final String ENV_VAR = System.getenv("RUNWAY_ML_TOKEN");
    private RestTemplate restTemplate;

    public void process(String data){
        // convert image to base64
        // extract prompt
        // send to process at post to runway service
    }
    // call api service to runway gen3ai api
    private ResponseEntity<String> postToRunwayService(String prompt, String base64image){

        RunwayRequest runwayRequest = new RunwayRequest();
        runwayRequest.setPromptText(prompt);
        runwayRequest.setPromptImage(base64image);

        String apiUrl = "https://api.dev.runwayml.com/v1/image_to_video";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + ENV_VAR);
        headers.set("X-Runway-Version", "2024-11-06");

        HttpEntity<RunwayRequest> entity = new HttpEntity<>(runwayRequest, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

        return ResponseEntity.ok(response.getBody());
    }

}
