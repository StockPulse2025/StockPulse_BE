package com.stockpulse.stockpulseAPI.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

public class NotificationRequestDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "알림 설정 업데이트 요청 DTO")
    public static class NotificationUpdateRequestDTO {
        @Schema(description = "보유 종목에 대한 알림 여부")
        private Boolean ownStock;

        @Schema(description = "관심 종목에 대한 알림 여부")
        private Boolean interestStock;

        @Schema(description = "긍정 뉴스 알림 여부")
        private Boolean goodNews;

        @Schema(description = "부정 뉴스 알림 여부")
        private Boolean badNews;

        @Schema(description = "중립 뉴스 알림 여부")
        private Boolean neutralNews;

        @Schema(description = "최소 긍정 영향도")
        private BigDecimal goodSensitivity1;

        @Schema(description = "최소 부정 영향도")
        private BigDecimal badSensitivity1;

        @Schema(description = "최대 긍정 영향도")
        private BigDecimal goodSensitivity2;

        @Schema(description = "최대 부정 민감도")
        private BigDecimal badSensitivity2;
    }
}
