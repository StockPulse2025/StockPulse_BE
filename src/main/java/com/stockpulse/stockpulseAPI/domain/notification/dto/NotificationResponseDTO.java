package com.stockpulse.stockpulseAPI.domain.notification.dto;

import com.stockpulse.stockpulseAPI.domain.notification.entity.Notification;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class NotificationResponseDTO {
    private List<NotificationItemResponseDTO> notificationItems;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NotificationItemResponseDTO {
        private Long id;
        private Long newsId;
        private String stockName;
        private BigDecimal impactRate;
        private String title;
        private Long stockId;
        private String stockImgUrl;
        private String createdAt;
    }

    public NotificationResponseDTO toResponseDTO(List<Notification> notifications) {
        for (Notification notification : notifications) {
            NotificationItemResponseDTO notificationItem = NotificationItemResponseDTO.builder()
                            .id(notification.getId())
                            .newsId(notification.getImpact().getNews().getId())
                            .stockName(notification.getImpact().getStock().getName())
                            .impactRate(notification.getImpact().getImpactRate())
                            .title(notification.getImpact().getNews().getTitle())
                            .stockId(notification.getImpact().getStock().getId())
                            .stockImgUrl(notification.getImpact().getStock().getImageUrl())
                            .createdAt(notification.getCreatedAt().toString())
                            .build();

            notificationItems.add(notificationItem);
        }
        return this;
    }
}
