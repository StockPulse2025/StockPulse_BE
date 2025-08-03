package com.stockpulse.stockpulseAPI.domain.notification.service;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.notification.dto.NotificationRequestDTO;
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

    @Transactional(readOnly = true) // 조회 성능 최적화 : dirty checking 비활성화
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


    @Transactional
    public NotificationResponseDTO.NotificationSettingResponseDTO updateNotificationSettings(Long userId, NotificationRequestDTO.NotificationUpdateRequestDTO dto) {
        NotificationSetting setting = notificationSettingRepository.findByMemberId(userId)
                .orElseThrow(() -> new IllegalArgumentException("알림 설정이 존재하지 않습니다."));

        // null 체크해서 값이 있을 때만 업데이트 (부분 수정)
        if (dto.getOwnStock() != null) setting.setOwnStock(dto.getOwnStock());
        if (dto.getInterestStock() != null) setting.setInterestStock(dto.getInterestStock());
        if (dto.getGoodNews() != null) setting.setGoodNews(dto.getGoodNews());
        if (dto.getBadNews() != null) setting.setBadNews(dto.getBadNews());
        if (dto.getNeutralNews() != null) setting.setNeutralNews(dto.getNeutralNews());
        if (dto.getGoodSensitivity1() != null) setting.setGoodSensitivity1(dto.getGoodSensitivity1());
        if (dto.getBadSensitivity1() != null) setting.setBadSensitivity1(dto.getBadSensitivity1());
        if (dto.getGoodSensitivity2() != null) setting.setGoodSensitivity2(dto.getGoodSensitivity2());
        if (dto.getBadSensitivity2() != null) setting.setBadSensitivity2(dto.getBadSensitivity2());

        // JPA 영속성 컨텍스트에서 가져온 데이터
        return getNotificationSettings(userId);

        // 트랜잭션 끝나면 JPA dirty checking으로 자동 업데이트 됨
    }


    @Transactional
    public void resetNotificationSetting(Long memberId) {
        NotificationSetting notificationSetting = notificationSettingRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("알림 설정이 존재하지 않습니다."));

        notificationSetting.setGoodNews(true);
        notificationSetting.setBadNews(false);
        notificationSetting.setNeutralNews(false);
        notificationSetting.setOwnStock(true);
        notificationSetting.setInterestStock(true);
        notificationSetting.setGoodSensitivity1(BigDecimal.valueOf(2.0));
        notificationSetting.setBadSensitivity1(BigDecimal.valueOf(5.0));
        notificationSetting.setGoodSensitivity2(BigDecimal.valueOf(5.0));
        notificationSetting.setBadSensitivity2(BigDecimal.valueOf(10.0));
    }

}
