package com.stockpulse.stockpulseAPI.domain.notification.controller;

import com.stockpulse.stockpulseAPI.domain.notification.dto.NotificationRequestDTO;
import com.stockpulse.stockpulseAPI.domain.notification.dto.NotificationResponseDTO;
import com.stockpulse.stockpulseAPI.domain.notification.service.NotificationSettingService;
import com.stockpulse.stockpulseAPI.global.apiPayload.ApiResponse;
import com.stockpulse.stockpulseAPI.global.security.handler.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "알림 설정 일부 업데이트", description = "로그인한 사용자의 알림 설정을 부분적으로 수정합니다.")
    @PatchMapping("/settings")
    public ApiResponse<NotificationResponseDTO.NotificationSettingResponseDTO> updateNotificationSettings(
            @AuthUser Long userId,
            @RequestBody NotificationRequestDTO.NotificationUpdateRequestDTO updateRequest) {
        NotificationResponseDTO.NotificationSettingResponseDTO updatedSettings =
                notificationSettingService.updateNotificationSettings(userId, updateRequest);
        return ApiResponse.onSuccess(updatedSettings);
    }

    @Operation(summary = "알림 설정 초기화", description = "로그인한 사용자 알림 설정 초기화")
    @PostMapping("/settings/reset")
    public ApiResponse<Void> resetNotificationSettings(@AuthUser Long userId) {
        notificationSettingService.resetNotificationSetting(userId);
        return ApiResponse.onSuccess(null);
    }
}
