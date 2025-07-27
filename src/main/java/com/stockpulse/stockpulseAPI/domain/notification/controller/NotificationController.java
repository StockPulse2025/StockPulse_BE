package com.stockpulse.stockpulseAPI.domain.notification.controller;

import com.stockpulse.stockpulseAPI.domain.notification.dto.NotificationResponseDTO;
import com.stockpulse.stockpulseAPI.domain.notification.service.NotificationSettingService;
import com.stockpulse.stockpulseAPI.global.apiPayload.ApiResponse;
import com.stockpulse.stockpulseAPI.global.security.handler.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
@Tag(name = "Notification", description = "알림 설정 관련 API")
public class NotificationController {
    private final NotificationSettingService notificationSettingService;

    @Operation(summary = "알림 설정 조회", description = "현재 로그인한 사용자의 알림 설정을 조회합니다.")
    @GetMapping("/settings")
    public ApiResponse<NotificationResponseDTO.NotificationSettingResponseDTO> getNotificationSettings(@AuthUser Long userId) {
        return ApiResponse.onSuccess(notificationSettingService.getNotificationSettings(userId));
    }
}
