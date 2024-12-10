package com.thyme.ai.thymeai.controller;

import com.thyme.ai.thymeai.model.UserData;
import com.thyme.ai.thymeai.service.ImageToVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Controller
public class PushNotificationController {

    private final ImageToVideoService imageToVideoService;

    @Autowired
    public PushNotificationController(ImageToVideoService imageToVideoService) {
        this.imageToVideoService = imageToVideoService;
    }

    public void pushNotification(){

        UserData userData = imageToVideoService.postProcessingFunction();

        if(userData == null) {
            return;
        }
        // Prepare HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + System.getenv("LINE_BOT_CHANNEL_TOKEN"));

        // Prepare the request body
        Map<String, Object> message = new HashMap<>();
        message.put("type", "video");
        message.put("originalContentUrl", userData.getVideoId());
        message.put("previewImageUrl", userData.getUserImagePrompt());
        message.put("trackingId", "track-id"); // Optional, add if required

        Map<String, Object> body = new HashMap<>();
        body.put("to", userData.getId());
        body.put("messages", new Map[]{message});

        // Wrap in HttpEntity
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();

        // SEND POST REQUEST ==>
        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.line.me/v2/bot/message/push",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // Handle response
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Push notification sent successfully: " + response.getBody());
        } else {
            System.err.println("Failed to send push notification: " + response.getBody());
        }
    }

}
