package com.stockpulse.stockpulseAPI.domain.notification.service;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.notification.dto.NotificationResponseDTO;
import com.stockpulse.stockpulseAPI.domain.notification.entity.NotificationSetting;
import com.stockpulse.stockpulseAPI.domain.notification.repository.NotificationSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class NotificationSettingService {
    private final NotificationSettingRepository notificationSettingRepository;

    @Transactional(readOnly = true) // 조회 기능 성능 최적화 : dirty checking 비활성화
    public NotificationResponseDTO.NotificationSettingResponseDTO getNotificationSettings(Long userId) {
        return notificationSettingRepository.findByMemberId(userId).map(notificationSetting ->
            NotificationResponseDTO.NotificationSettingResponseDTO.builder()
                    .ownStock(notificationSetting.getOwnStock())
                    .interestStock(notificationSetting.getInterestStock())
                    .goodNews(notificationSetting.getGoodNews())
                    .badNews(notificationSetting.getBadNews())
                    .neutralNews(notificationSetting.getNeutralNews())
                    .goodSensitivity1(notificationSetting.getGoodSensitivity1())
                    .badSensitivity1(notificationSetting.getBadSensitivity1())
                    .goodSensitivity2(notificationSetting.getGoodSensitivity2())
                    .badSensitivity2(notificationSetting.getBadSensitivity2())
                    .build()
        )
                .orElseThrow(() -> new IllegalArgumentException("알림 세팅이 존재하지 않습니다."));
    }

    public void initNotificationSetting(Member member) {
        NotificationSetting notificationSetting = NotificationSetting.builder()
                .member(member)
                .goodNews(true)
                .badNews(false)
                .neutralNews(false)
                .ownStock(true)
                .interestStock(true)
                .goodSensitivity1(BigDecimal.valueOf(2.0))
                .badSensitivity1(BigDecimal.valueOf(5.0))
                .goodSensitivity2(BigDecimal.valueOf(5.0))
                .badSensitivity2(BigDecimal.valueOf(10.0))
                .build();

        notificationSettingRepository.save(notificationSetting);
    } // 회원 알림 설정 초기화
}
