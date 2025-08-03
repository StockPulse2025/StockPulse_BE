package com.stockpulse.stockpulseAPI.notification.service;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.member.repository.MemberRepository;
import com.stockpulse.stockpulseAPI.domain.notification.dto.NotificationRequestDTO;
import com.stockpulse.stockpulseAPI.domain.notification.dto.NotificationResponseDTO;
import com.stockpulse.stockpulseAPI.domain.notification.service.NotificationSettingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class NotificationSettingServiceTest {
    @Autowired private NotificationSettingService notificationSettingService;
    @Autowired private MemberRepository memberRepository;


    @Test
    @DisplayName("회원 알림 설정 초기화 및 조회 테스트")
    void getNotificationSettings() {
        // given
        Member member = memberRepository.save(
                Member.builder()
                        .nickname("핑핑이")
                        .email("pingping@example.com")
                        .build()
        );

        notificationSettingService.initNotificationSetting(member);


        // when
        NotificationResponseDTO.NotificationSettingResponseDTO notificationSettings = notificationSettingService.getNotificationSettings(member.getId());

        // then
        assertThat(notificationSettings).isNotNull();
        assertThat(notificationSettings.getOwnStock()).isTrue();
        assertThat(notificationSettings.getInterestStock()).isTrue();
        assertThat(notificationSettings.getGoodNews()).isTrue();
        assertThat(notificationSettings.getBadNews()).isFalse();
        assertThat(notificationSettings.getGoodSensitivity1()).isEqualByComparingTo(BigDecimal.valueOf(2.0));
        assertThat(notificationSettings.getBadSensitivity1()).isEqualByComparingTo(BigDecimal.valueOf(5.0));
        assertThat(notificationSettings.getGoodSensitivity2()).isEqualByComparingTo(BigDecimal.valueOf(5.0));
        assertThat(notificationSettings.getBadSensitivity2()).isEqualByComparingTo(BigDecimal.valueOf(10.0));
    }

    @Test
    @DisplayName("회원 알림 설정 업데이트")
    void updateNotificationSettings() {
        // given
        Member member = memberRepository.save(
                Member.builder()
                        .nickname("핑핑이")
                        .email("pingping@example.com")
                        .build()
        );

        notificationSettingService.initNotificationSetting(member);

        // when
        NotificationRequestDTO.NotificationUpdateRequestDTO dto = NotificationRequestDTO.NotificationUpdateRequestDTO.builder()
                .ownStock(false)
                .neutralNews(true)
                .goodSensitivity1(BigDecimal.valueOf(25.0))
                .goodSensitivity2(BigDecimal.valueOf(50.0))
                .badSensitivity1(BigDecimal.valueOf(20.0))
                .badSensitivity2(BigDecimal.valueOf(30.0))
                .build();

        NotificationResponseDTO.NotificationSettingResponseDTO responseDTO
                = notificationSettingService.updateNotificationSettings(member.getId(), dto);

        // then
        assertThat(responseDTO.getOwnStock()).isFalse();
        assertThat(responseDTO.getNeutralNews()).isTrue();
        assertThat(responseDTO.getGoodSensitivity1()).isEqualByComparingTo(BigDecimal.valueOf(25.0));
        assertThat(responseDTO.getGoodSensitivity2()).isEqualByComparingTo(BigDecimal.valueOf(50.0));
        assertThat(responseDTO.getBadSensitivity1()).isEqualByComparingTo(BigDecimal.valueOf(20.0));
        assertThat(responseDTO.getBadSensitivity2()).isEqualByComparingTo(BigDecimal.valueOf(30.0));
    }
}
