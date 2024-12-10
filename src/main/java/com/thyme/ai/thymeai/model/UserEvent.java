package com.thyme.ai.thymeai.model;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent {

    private String destination;
    private List<LineEvent> events;
}
