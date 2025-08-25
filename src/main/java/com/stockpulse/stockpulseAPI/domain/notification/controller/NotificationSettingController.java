package com.stockpulse.stockpulseAPI.domain.notification.controller;

import com.stockpulse.stockpulseAPI.domain.notification.dto.NotificationSettingRequestDTO;
import com.stockpulse.stockpulseAPI.domain.notification.dto.NotificationSettingResponseDTO;
import com.stockpulse.stockpulseAPI.domain.notification.service.NotificationService;
import com.stockpulse.stockpulseAPI.global.apiPayload.ApiResponse;
import com.stockpulse.stockpulseAPI.global.security.handler.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification/setting")
@RequiredArgsConstructor
@Tag(name = "NotificationSetting", description = "알림 설정 관련 API")
public class NotificationSettingController {

    private final NotificationService notificationService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<NotificationSettingResponseDTO>> getSetting(@AuthUser Long userId) {

        NotificationSettingResponseDTO response = notificationService.getNotificationSetting(userId);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @PatchMapping("")
    @Operation(summary = "알림 설정 수정",
            description = "사용자가 알림 설정을 변경합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "알림 설정 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NotificationSettingRequestDTO.UpdateDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "ownStock": true,
                                      "interestStock": true,
                                      "goodNews": true,
                                      "badNews": false,
                                      "neutralNews": true,
                                      "goodSensitivity1": 0.5,
                                      "badSensitivity1": 0.5,
                                      "goodSensitivity2": 0.8,
                                      "badSensitivity2": 0.8    
                                    }
                                    """
                            )
                    )
            )
    )
    public ResponseEntity<ApiResponse<String>> updateSetting(@AuthUser Long userId, @RequestBody NotificationSettingRequestDTO.UpdateDTO request) {
        notificationService.updateNotificationSetting(userId, request);
        return ResponseEntity.ok(ApiResponse.onSuccess("success"));
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<String>> resetSetting(@AuthUser Long userId) {
        notificationService.resetNotification(userId);

        return ResponseEntity.ok(ApiResponse.onSuccess("success"));
    }


}
