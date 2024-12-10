package com.thyme.ai.thymeai.controller;

import com.linecorp.bot.messaging.client.MessagingApiClient;
import com.linecorp.bot.messaging.model.ReplyMessageRequest;
import com.linecorp.bot.messaging.model.TextMessage;
import com.linecorp.bot.spring.boot.handler.annotation.EventMapping;
import com.linecorp.bot.spring.boot.handler.annotation.LineMessageHandler;
import com.linecorp.bot.webhook.model.MessageEvent;
import com.linecorp.bot.webhook.model.TextMessageContent;
import com.thyme.ai.thymeai.model.UserData;
import com.thyme.ai.thymeai.model.UserEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/v1/api")
@LineMessageHandler
public class ThymeAIController {

    private LinkedList<UserData> userDataArr = new LinkedList<>();
    private final MessagingApiClient messagingApiClient;

    private static final String STATE_IMAGE = "IMAGE";
    private static final String STATE_TEXT = "TEXT";
    private static final String STATE_PROCESS = "PROCESS";
    private static final String STATE_DONE = "DONE";

    @Autowired
    public ThymeAIController(MessagingApiClient messagingApiClient) {
        this.messagingApiClient = messagingApiClient;
    }

    @PostMapping("/add") // this end point for testing only.
    public ResponseEntity<?> post(@RequestBody UserEvent userEvent) {

        String id = userEvent.getEvents().get(0).getSource().getUserId();
        String contentType = userEvent.getEvents().get(0).getMessage().getType();
        String userTextPrompt = "";
        String userImagePrompt = "";

        if(id == null) {
            System.err.println("id cannot be null");
            // return false;
        }

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
            return ResponseEntity.ok().body("NOT OK");
        }

        /** if not then send request to runway */
        // 1. verify type of content if type = image then --> convert as base64 first then put in userImagePrompt
        if (contentType.equals("image")) {
            System.out.println("content type: image");

            if (userData != null && !userData.getUserImagePrompt().isEmpty()) {
                System.out.println("Please add TEXT prompt because you are already add Image or type CANCEL to clear operation.");
                return ResponseEntity.ok().body("Please add TEXT prompt");
            }

            userImagePrompt = "++";

            if (userData == null) {
                System.out.println("userData is null at image");
                userData = new UserData(id, false, STATE_IMAGE, userImagePrompt, "");
            }

            userData.setUserImagePrompt(userImagePrompt);
        } else if (contentType.equalsIgnoreCase("text")) {

            if (userData != null && !userData.getUserTextPrompt().isEmpty()) {
                System.out.println("Please add IMAGE reference because you are already add Text or type CANCEL to clear operatio.");
                return ResponseEntity.ok().body("Please add IMAGE reference");
            }

            userTextPrompt = userEvent.getEvents().get(0).getMessage().getText();
            System.out.println("textPrompt : " + userTextPrompt);

            if(userData == null) {
                userData = new UserData(id, false, STATE_TEXT, "", userTextPrompt);
            }

            userData.setUserTextPrompt(userTextPrompt);
        } else {
            // sticker or something else ...
            return ResponseEntity.ok().body("type not support");
        }

        if(!userData.getUserImagePrompt().isEmpty() && !userData.getUserTextPrompt().isEmpty()) {
            userData.setState(STATE_PROCESS);
        }

        try {
            if(isInQueue) {
                userDataArr.set(index, userData);
            } else {
                userDataArr.add(userData);
            }
        } catch (Exception e) {
            System.err.println("Cannot update index due to exception");
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(userData.toString());
    }

    // return data to the caller
    @EventMapping
    public void handleTextMessageEvent(MessageEvent event) {
        System.out.println("event: " + event);
        if (event.message() instanceof TextMessageContent message) {
            final String originalMessageText = message.text();
            messagingApiClient.replyMessage(new ReplyMessageRequest(
                    event.replyToken(),
                    //List.of(new TextMessage(originalMessageText)),
                    List.of(new TextMessage("RECEIVED!")),
                    false));
        }
    }

}
