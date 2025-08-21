package com.stockpulse.stockpulseAPI.global.KIS.KISWebSocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
@RequiredArgsConstructor
public class KISWebSocketClientConfig {

    private final KISWebSocketHandler1 kisWebSocketHandler1;
    private final KISWebSocketHandler2 kisWebSocketHandler2;

    private static final String WS_URI = "ws://ops.koreainvestment.com:21000/tryitout/H0STCNT0";

    private WebSocketConnectionManager manager1;
    private WebSocketConnectionManager manager2;

    public void startConnections() {
        if (manager1 == null || !manager1.isRunning()) {
            StandardWebSocketClient client1 = new StandardWebSocketClient();
            manager1 = new WebSocketConnectionManager(client1, kisWebSocketHandler1, WS_URI);
            manager1.setAutoStartup(false);
            manager1.start();
        }

        if (manager2 == null || !manager2.isRunning()) {
            StandardWebSocketClient client2 = new StandardWebSocketClient();
            manager2 = new WebSocketConnectionManager(client2, kisWebSocketHandler2, WS_URI);
            manager2.setAutoStartup(false);
            manager2.start();
        }
    }

    public void stopConnections() {
        if (manager1 != null && manager1.isRunning()) {
            manager1.stop();
        }
        if (manager2 != null && manager2.isRunning()) {
            manager2.stop();
        }
    }
}