package com.stockpulse.stockpulseAPI.domain.notification.dto;
import lombok.Getter;

import java.math.BigDecimal;

public class NotificationSettingRequestDTO {

    @Getter
    public static class UpdateDTO {
        private Boolean ownStock;
        private Boolean interestStock;
        private Boolean goodNews;
        private Boolean badNews;
        private Boolean neutralNews;
        private BigDecimal goodSensitivity1;
        private BigDecimal goodSensitivity2;
        private BigDecimal badSensitivity1;
        private BigDecimal badSensitivity2;
    }
}
