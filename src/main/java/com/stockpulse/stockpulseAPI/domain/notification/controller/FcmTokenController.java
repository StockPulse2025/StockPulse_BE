package com.stockpulse.stockpulseAPI.domain.notification.controller;

import com.stockpulse.stockpulseAPI.domain.notification.dto.FcmTokenRequestDto;
import com.stockpulse.stockpulseAPI.domain.notification.service.FcmTokenservice;
import com.stockpulse.stockpulseAPI.global.security.handler.annotation.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.web.servlet.function.ServerResponse.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FcmTokenController {

    // TODO : service 클래스
    private final FcmTokenservice fcmTokenService;

    // TODO : 사용자 토큰 등록 api

    @PostMapping
    @RequestMapping("/token")
    public ResponseEntity<?> registerFcmToken(@AuthUser Long userId, @RequestBody FcmTokenRequestDto dto) {
        fcmTokenService.registerFcmToken(userId, dto);

        return ResponseEntity.ok().body(Map.of("message", "success"));
    }

    // TODO : 사용자 토큰 삭제 api

    // TODO :
}
