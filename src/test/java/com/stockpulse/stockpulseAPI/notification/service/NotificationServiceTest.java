package com.stockpulse.stockpulseAPI.notification.service;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import com.stockpulse.stockpulseAPI.domain.news.entity.Impact;
import com.stockpulse.stockpulseAPI.domain.news.repository.ImpactRepository;
import com.stockpulse.stockpulseAPI.domain.notification.entity.FcmToken;
import com.stockpulse.stockpulseAPI.domain.notification.entity.NotificationSetting;
import com.stockpulse.stockpulseAPI.domain.notification.fcm.FcmClient;
import com.stockpulse.stockpulseAPI.domain.notification.repository.FcmTokenRepository;
import com.stockpulse.stockpulseAPI.domain.notification.repository.NotificationSettingRepository;
import com.stockpulse.stockpulseAPI.domain.notification.service.NotificationService;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import com.stockpulse.stockpulseAPI.domain.stock.repository.MemberFavoriteStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private FcmTokenRepository fcmTokenRepository = mock(FcmTokenRepository.class);
    private FcmClient fcmClient = mock(FcmClient.class);
    private NotificationSettingRepository notificationSettingRepository = mock(NotificationSettingRepository.class);
    private MemberFavoriteStockRepository memberFavoriteStockRepository = mock(MemberFavoriteStockRepository.class);
    private ImpactRepository impactRepository = mock(ImpactRepository.class);
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(
                fcmTokenRepository,
                fcmClient,
                notificationSettingRepository,
                memberFavoriteStockRepository,
                impactRepository
        );
    }

    @Test
    void testNotification_sendNotificationWhenConditionMet() {
        // given
        Stock stock = Stock.builder().id(1L).name("삼성전자").build();
        News news = News.builder().id(1L).title("삼성전자 급등!").build();

        Impact impact = Impact.builder()
                .stock(stock)
                .news(news)
                .impactRate(BigDecimal.valueOf(3.0)) // 양수: goodNews 케이스
                .build();

        Member member = Member.builder().id(1L).nickname("핑핑이").build();

        NotificationSetting setting = NotificationSetting.builder()
                .member(member)
                .goodNews(true)
                .badNews(false)
                .neutralNews(false)
                .goodSensitivity1(BigDecimal.valueOf(2.0))
                .badSensitivity1(BigDecimal.valueOf(5.0))
                .goodSensitivity2(BigDecimal.valueOf(5.0))
                .badSensitivity2(BigDecimal.valueOf(10.0))
                .build();

        FcmToken token = FcmToken.builder()
                .member(member)
                .fcmToken("fake-fcm-token")
                .build();

        // mocking repository return values
        when(memberFavoriteStockRepository.findMembersByStock(stock)).thenReturn(Optional.of(List.of(member)));
        when(notificationSettingRepository.findByMember(member)).thenReturn(Optional.of(setting));
        when(fcmTokenRepository.findByMember(member)).thenReturn(Optional.of(token));
        when(impactRepository.findAll()).thenReturn(List.of(impact));

        // when
        notificationService.notification();

        // then
        verify(fcmClient, times(1)).sendNotification(eq("fake-fcm-token"), contains("삼성전자"), contains("급등"));
    }
}

