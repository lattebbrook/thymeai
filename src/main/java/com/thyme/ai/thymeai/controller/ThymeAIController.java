package com.thyme.ai.thymeai.controller;

import com.linecorp.bot.messaging.client.MessagingApiClient;
import com.linecorp.bot.messaging.model.ReplyMessageRequest;
import com.linecorp.bot.messaging.model.TextMessage;
import com.linecorp.bot.spring.boot.handler.annotation.EventMapping;
import com.linecorp.bot.spring.boot.handler.annotation.LineMessageHandler;
import com.linecorp.bot.webhook.model.ImageMessageContent;
import com.linecorp.bot.webhook.model.MessageEvent;
import com.linecorp.bot.webhook.model.TextMessageContent;
import com.thyme.ai.thymeai.service.ImageToVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** @author Tanawat Boonmak */

@Controller
@RequestMapping("/v1/api")
@LineMessageHandler
public class ThymeAIController {

    private final MessagingApiClient messagingApiClient;
    private final ImageToVideoService imageToVideoService;

    @Autowired
    public ThymeAIController(MessagingApiClient messagingApiClient, ImageToVideoService imageToVideoService) {
        this.messagingApiClient = messagingApiClient;
        this.imageToVideoService = imageToVideoService;
    }

    // return data to the caller
    @EventMapping
    public void handleTextMessageEvent(MessageEvent event) {
        System.out.println("[DEBUG]: " + event);

        if (event.message() instanceof ImageMessageContent image) {
            messagingApiClient.replyMessage(new ReplyMessageRequest(
                    event.replyToken(),
                    List.of(new TextMessage(imageToVideoService.process(event, image, null))),
                    false));
            return;
        }

        if (event.message() instanceof TextMessageContent message) {
            if (message.text().equalsIgnoreCase("#STOP")) {
                messagingApiClient.replyMessage(new ReplyMessageRequest(
                        event.replyToken(),
                        List.of(new TextMessage(imageToVideoService.clearProcess(event.source().userId()))),
                        false));
                return;
            }

            messagingApiClient.replyMessage(new ReplyMessageRequest(
                    event.replyToken(),
                    List.of(new TextMessage(imageToVideoService.process(event, null, message))),
                    false));
        }

    }

}
