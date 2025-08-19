package com.stockpulse.stockpulseAPI.domain.notification.dto;

import com.stockpulse.stockpulseAPI.domain.notification.entity.NotificationSetting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSettingResponseDTO {

    private Long id;
    private boolean ownStock;
    private boolean interestStock;
    private boolean goodNews;
    private boolean badNews;
    private boolean neutralNews;
    private BigDecimal goodSensitivity1;
    private BigDecimal goodSensitivity2;
    private BigDecimal badSensitivity1;
    private BigDecimal badSensitivity2;

    public NotificationSettingResponseDTO toResponseDTO(NotificationSetting notificationSetting) {
        return NotificationSettingResponseDTO.builder()
                .id(notificationSetting.getId())
                .ownStock(notificationSetting.getOwnStock())
                .interestStock(notificationSetting.getInterestStock())
                .goodNews(notificationSetting.getGoodNews())
                .badNews(notificationSetting.getBadNews())
                .neutralNews(notificationSetting.getNeutralNews())
                .goodSensitivity1(notificationSetting.getGoodSensitivity1())
                .goodSensitivity2(notificationSetting.getGoodSensitivity2())
                .badSensitivity1(notificationSetting.getBadSensitivity1())
                .badSensitivity2(notificationSetting.getBadSensitivity2())
                .build();
    }
}
