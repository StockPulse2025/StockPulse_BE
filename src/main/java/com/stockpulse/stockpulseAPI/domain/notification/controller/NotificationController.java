package com.stockpulse.stockpulseAPI.domain.notification.controller;

import com.stockpulse.stockpulseAPI.domain.notification.dto.NotificationResponseDTO;
import com.stockpulse.stockpulseAPI.domain.notification.service.NotificationService;
import com.stockpulse.stockpulseAPI.global.apiPayload.ApiResponse;
import com.stockpulse.stockpulseAPI.global.security.handler.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
@Tag(name = "Notification", description = "알림 이력 조회 관련 API")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/history")
    @Operation(
            summary = "알림 이력 조회",
            description = "알림 이력을 최근 순서대로 조회합니다. 관심 목록(interest), 보유 목록(owned)를 필터링하여 조회합니다." ,
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "type",
                            description = "종목 필터링",
                            required = true,
                            schema = @io.swagger.v3.oas.annotations.media.Schema(type = "string", allowableValues = {"owned", "interest"}),
                            example = "owned"
                    )
            }
    )
    public ResponseEntity<ApiResponse<NotificationResponseDTO>> getNotificationHistory(
            @AuthUser Long userId,
            @RequestParam("type") String type) {
        NotificationResponseDTO response = notificationService.getNotificationHistory(userId, type);

        return ResponseEntity
                .ok(ApiResponse.onSuccess(response));
    }
}
