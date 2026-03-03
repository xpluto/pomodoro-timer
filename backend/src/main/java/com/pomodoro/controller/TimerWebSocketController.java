package com.pomodoro.controller;

import com.pomodoro.dto.TimerSyncMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class TimerWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/timer/sync")
    public void syncTimer(TimerSyncMessage message) {
        messagingTemplate.convertAndSend("/topic/timer/" + message.getUserId(), message);
    }
}
