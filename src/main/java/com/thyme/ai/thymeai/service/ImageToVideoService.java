package com.thyme.ai.thymeai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.webhook.model.ImageMessageContent;
import com.linecorp.bot.webhook.model.MessageEvent;
import com.linecorp.bot.webhook.model.TextMessageContent;
import com.thyme.ai.thymeai.config.EnvironmentVariable;
import com.thyme.ai.thymeai.model.RunwayRequest;
import com.thyme.ai.thymeai.model.RunwayResponse;
import com.thyme.ai.thymeai.model.UserData;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.jayway.jsonpath.JsonPath;

import java.util.*;

@Service
public class ImageToVideoService {


    private RestTemplate restTemplate;

    private static final String STATE_IMAGE = "IMAGE";
    private static final String STATE_TEXT = "TEXT";
    private static final String STATE_PROCESS = "PROCESS";
    private static final String STATE_DONE = "DONE";

    private LinkedList<UserData> userDataArr = new LinkedList<>();

    public String process(MessageEvent messageEvent, ImageMessageContent imageMessageContent, TextMessageContent textMessage) {
        String id = messageEvent.source().userId();
        String contentType = imageMessageContent == null ? "text" : "image";

        String userTextPrompt;
        String userImagePrompt;

        if(id == null) {
            System.err.println("id cannot be null");
            // return false;
        }

        boolean isImageAttached = false;

        UserData userData = null;
        int index = 0;
        boolean isInQueue = false;


        for(int i = 0; i < userDataArr.size(); i++) {
            if(userDataArr.get(i).getId().equalsIgnoreCase(id)) {
                userData = userDataArr.get(i);
                index = i;
                isInQueue = true;
            }
        }

        if(userData != null && userData.getState().equalsIgnoreCase(STATE_PROCESS)) {
            // return false and log still process please wait
            System.err.println("still process please wait");
            return "ขออภัยจ้า ตอนนี้น้อง AI กำลังทำการ Process อยู่นะคร๊าบ";
        }

        /** if not then send request to runway */
        // 1. verify type of content if type = image then --> convert as base64 first then put in userImagePrompt
        if (contentType.equals("image")) {
            System.out.println("content type: image");

            if (userData != null && !userData.getUserImagePrompt().isEmpty()) {
                System.out.println("Please add TEXT prompt because you are already add Image or type CANCEL to clear operation.");
                return "ขออภัยด้วยนะครับ เนื่องจากน้องได้รับรูปภาพแล้ว จึงขอให้พี่ช่วยส่ง Prompt ที่ต้องการ Animate รูปภาพนี้มาได้มั้ยคร๊าบ";
            }

            userImagePrompt = "++";

            if (userData == null) {
                System.out.println("userData is null at image");
                userData = new UserData(id, false, STATE_IMAGE, "", "", "", "");
            }

            userData.setUserImagePrompt(userImagePrompt);
            isImageAttached = true;
        } else {

            if (userData != null && !userData.getUserTextPrompt().isEmpty()) {
                System.out.println("Please add IMAGE reference because you are already add Text or type CANCEL to clear operation.");
                return "ขออภัยด้วยนะครับ เนื่องจากน้องได้รับ Prompt แล้ว จึงขอให้พี่ช่วยส่งรูปภาพที่ต้องการ Animate มาได้มั้ยคร๊าบ";
            }

            userTextPrompt = textMessage.text();
            System.out.println("textPrompt : " + userTextPrompt);

            if(userData == null) {
                userData = new UserData(id, false, STATE_TEXT, "", "", "", "");
            }

            userData.setUserTextPrompt(userTextPrompt);
        }

        try {
            if(!userData.getUserImagePrompt().isEmpty() && !userData.getUserTextPrompt().isEmpty()) {
                userData.setState(STATE_PROCESS);
                // send userdata into business process
                postToRunwayService(index, userData);
                return "ได้รับข้อมูลแล้วครบถ้วนทั้งรูปภาพและ Prompt แล้วคร๊าบ รบกวนพี่ๆ รอไม่เกิน 45 วินาทีนะคร๊าบบบ... (กำลังประมวลผล)";
            }
        } catch (Exception e) {
            System.err.println("Cannot send request to runway due to exception");
            e.printStackTrace();
        }

        if(isInQueue) {
            userDataArr.set(index, userData);
        } else {
            userDataArr.add(userData);
        }

        return isImageAttached ? "ได้รับข้อมูลรูปภาพแล้วคร๊าบบบ รบกวนเขียน Prompt ให้น้องหน่อยน๊า" : "ขอบคุณคร๊าบบ รบกวนแนบรูปภาพให้น้อง AI ด้วยนะครับ";
    }

    public String clearProcess(String userId){
        boolean isRemovable = true;
        String lastStatus = "";
        int indice = 0;
        for(int i = 0; i < userDataArr.size(); i++) {
            if(userDataArr.get(i).getId().equals(userId)) {
                lastStatus = userDataArr.get(i).getState();
                if(!userDataArr.get(i).getState().equals(STATE_PROCESS) ||
                        !userDataArr.get(i).getState().equals(STATE_DONE)) {
                    indice = i;
                } else {
                    isRemovable = false;
                }
                break;
            }
        }

        userDataArr.remove(indice);
        return isRemovable ? "ยกเลิกข้อมูลที่ส่งไปล่าสุด." : "ไม่สามารถยกเลิกข้อมูลได้เนื่องจากได้ประมวลผลไปแล้ว สถานะล่าสุดคือ " + lastStatus;
    }

    // call api service to runway gen3ai api
    @Async
    private void postToRunwayService(int index, UserData userData) throws InterruptedException {

        RunwayRequest runwayRequest = new RunwayRequest();
        runwayRequest.setPromptText(userData.getUserTextPrompt());
        runwayRequest.setPromptImage(userData.getUserImagePrompt());

        String apiUrl = "https://api.dev.runwayml.com/v1/image_to_video";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + EnvironmentVariable.RUNWAY_ML_TOKEN);
        headers.set("X-Runway-Version", "2024-11-06");

        HttpEntity<RunwayRequest> entity = new HttpEntity<>(runwayRequest, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
        ResponseEntity.ok(response.getBody());

        // Parse response with JsonPath
        try {
            String id = JsonPath.read(response.getBody(), "$.id");
            System.out.println("[DEBUG] UserId: " + userData.getId() + " has extracted video ID: " + id);
            userData.setVideoId(id);
            userData.setState(STATE_PROCESS);
            userDataArr.set(index, userData);
        } catch (Exception e) {
            System.err.println("[ERROR] UserId: " + userData.getId() + ", Error extracting ID: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "*/45 * * * * *") // Every 45 seconds
    public UserData postProcessingFunction(){
        Queue<UserData> queue = new LinkedList<>();
        userDataArr.forEach(i -> {
            if(i.getState().equals(STATE_PROCESS)){
                queue.add(i);
            }
        });

        if(queue.isEmpty()) {
            // for demo only
            return null;
        }

        while(!queue.isEmpty()) {
            String outputUrl = "";
            String apiUrl = "https://api.dev.runwayml.com/v1/image_to_video/" + queue.peek().getVideoId();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + EnvironmentVariable.RUNWAY_ML_TOKEN);
            headers.set("X-Runway-Version", "2024-11-06");

            HttpEntity<RunwayRequest> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            ResponseEntity.ok(response.getBody());

            // Parse the response to extract "output"
            ObjectMapper objectMapper = new ObjectMapper();
            RunwayResponse runwayResponse;

            try {
                // Map JSON response to RunwayResponse DTO
                runwayResponse = objectMapper.readValue(response.getBody(), RunwayResponse.class);

                if(!runwayResponse.getStatus().equals("SUCCEEDED")) {
                    return null;
                }

                // Extract the output URLs
                if (runwayResponse.getOutput() != null && !runwayResponse.getOutput().isEmpty()) {
                    outputUrl = runwayResponse.getOutput().get(0); // Get the first URL
                    System.out.println("Generated Video URL: " + outputUrl);
                    // process it send to user
                } else {
                    System.out.println("No output found in response.");
                }

                // call to push notification eventHandler
                return queue.poll();
            } catch (Exception e) {
                System.err.println("Error parsing response: " + e.getMessage());
            }
        }

        // for demo only
        return null;
    }
}
