package com.stockpulse.stockpulseAPI.domain.notification.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FcmClient {
    public void sendNotification(String token, String title, String body) {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body).build()
                )
                .build();
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 메시지 전송 완료: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("FCM 메시지 전송 실패", e);
        }
    }
}
