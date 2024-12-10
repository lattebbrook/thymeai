package com.thyme.ai.thymeai.model;

import lombok.*;

import java.util.HashMap;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LineEvent {

    private String type;
    private String text;
    private Message message;
    private String webhookEventId;
    private DeliveryContext deliveryContext;
    private String timestamp;
    private Source source;
    private String replyToken;
    private String mode;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private String type;
        private String id;
        private String quoteToken;
        private String text;
    }

    @Getter
    public static class Source {
        private String type;
        private String userId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeliveryContext {
        private String isRedelivery;
    }

}
