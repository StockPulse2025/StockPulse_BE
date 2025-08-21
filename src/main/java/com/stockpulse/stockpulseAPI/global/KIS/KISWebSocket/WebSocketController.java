package com.stockpulse.stockpulseAPI.global.KIS.KISWebSocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/{symbol}")
    public String test(@DestinationVariable("symbol") String symbol, String message) {
        messagingTemplate.convertAndSend("/sub/" + symbol, message);
        return (message);
    }
}