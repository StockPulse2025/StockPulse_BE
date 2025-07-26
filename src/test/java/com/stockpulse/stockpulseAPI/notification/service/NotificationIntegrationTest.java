package com.stockpulse.stockpulseAPI.notification.service;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.member.repository.MemberRepository;
import com.stockpulse.stockpulseAPI.domain.news.entity.News;
import com.stockpulse.stockpulseAPI.domain.news.entity.NewsImpact;
import com.stockpulse.stockpulseAPI.domain.news.enums.Category;
import com.stockpulse.stockpulseAPI.domain.news.repository.NewsImpactRepository;
import com.stockpulse.stockpulseAPI.domain.news.repository.NewsRepository;
import com.stockpulse.stockpulseAPI.domain.notification.entity.FcmToken;
import com.stockpulse.stockpulseAPI.domain.notification.entity.NotificationSetting;
import com.stockpulse.stockpulseAPI.domain.notification.fcm.FcmClient;
import com.stockpulse.stockpulseAPI.domain.notification.repository.FcmTokenRepository;
import com.stockpulse.stockpulseAPI.domain.notification.repository.NotificationSettingRepository;
import com.stockpulse.stockpulseAPI.domain.notification.service.NotificationService;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import com.stockpulse.stockpulseAPI.domain.stock.entity.UserFavoriteStock;
import com.stockpulse.stockpulseAPI.domain.stock.repository.StockRepository;
import com.stockpulse.stockpulseAPI.domain.stock.repository.UserFavoriteStockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class NotificationIntegrationTest {

    @Autowired private NotificationService notificationService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private StockRepository stockRepository;
    @Autowired private NewsRepository newsRepository;
    @Autowired private NewsImpactRepository newsImpactRepository;
    @Autowired private NotificationSettingRepository notificationSettingRepository;
    @Autowired private FcmTokenRepository fcmTokenRepository;
    @Autowired private UserFavoriteStockRepository userFavoriteStockRepository;

    // 이건 실제로 외부 호출하지 않게 mock 처리 (알림 발송은 진짜 보내지 않도록!)
    @MockBean
    private FcmClient fcmClient;

    @Test
    void notification_shouldSendNotification_whenConditionsAreMet() {
        // given
        Member member = memberRepository.save(
                Member.builder()
                        .nickname("핑핑이")
                        .email("pingping@example.com") // 요거 추가!
                        .build()
        );
        Stock stock = stockRepository.save(
                Stock.builder()
                        .name("삼성전자")
                        .symbol("005930")
                        .build());

        News news = newsRepository.save(
                News.builder()
                        .title("삼성전자 급등!")
                        .url("https://www.naver.com")
                        .publisher("naver")
                        .publishedDate(LocalDateTime.now())
                        .category(Category.SEMICONDUCTOR_DISPLAY)
                        .build());

        NewsImpact newsImpact = newsImpactRepository.save(
                NewsImpact.builder()
                        .stock(stock)
                        .news(news)
                        .impactRate(BigDecimal.valueOf(3.0)) // 양수
                        .build()
        );

        UserFavoriteStock  userfavoriteStock = userFavoriteStockRepository.save(
                UserFavoriteStock.builder()
                .member(member)
                .stock(stock)
                .build());

        NotificationSetting notificationSetting = notificationSettingRepository.save(NotificationSetting.builder()
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
                .build());

        FcmToken fcmToken = fcmTokenRepository.save(FcmToken.builder()
                .member(member)
                .fcmToken("test-fcm-token")
                .build());

        // when
        notificationService.notification();

        // then
        verify(fcmClient, times(1))
                .sendNotification(eq("test-fcm-token"), contains("삼성전자"), contains("급등"));
    }
}
