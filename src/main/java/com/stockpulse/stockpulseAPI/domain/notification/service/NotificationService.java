package com.stockpulse.stockpulseAPI.domain.notification.service;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.news.entity.Impact;
import com.stockpulse.stockpulseAPI.domain.news.repository.ImpactRepository;
import com.stockpulse.stockpulseAPI.domain.notification.entity.FcmToken;
import com.stockpulse.stockpulseAPI.domain.notification.entity.NotificationSetting;
import com.stockpulse.stockpulseAPI.domain.notification.fcm.FcmClient;
import com.stockpulse.stockpulseAPI.domain.notification.repository.FcmTokenRepository;
import com.stockpulse.stockpulseAPI.domain.notification.repository.NotificationSettingRepository;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import com.stockpulse.stockpulseAPI.domain.stock.repository.MemberFavoriteStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final FcmTokenRepository fcmTokenRepository;
    private final FcmClient fcmClient;
    private final NotificationSettingRepository notificationSettingRepository;
    private final MemberFavoriteStockRepository memberFavoriteStockRepository;
    private final ImpactRepository impactRepository;


    public void notification() {
        /* TODO
         * 업데이트된 뉴스 영향도 가져오기
         * 영향도로 들어온 종목에 관심 있음을 등록한 사용자들을 찾는다.
         * 사용자들마다 알림 세팅을 조회하여 알림을 만족하는지 판단.
         * 해당 영향도 정보가 +, - 와 절대값을 가지고 사용자들의 알림 필터링 조건을 만족하는 판단.
         * 조건을 만족하는 사용자들의 FCM토큰을 조회 후, FCM 백엔드로 해당 영향도 관련 뉴스 내용 전송
         * */

        // 1. 업데이트된 뉴스 영향도 조회
//        List<NewsImpact> newsImpacts = newsImpactRepository.findRecentlyUpdated(LocalDateTime.now().minusMinutes(10));
        List<Impact> impacts = impactRepository.findAll();

        for (Impact impact : impacts) {
            Stock stock = impact.getStock();
            Double impactScore = impact.getImpactRate().doubleValue(); // ex: +5.3, -2.1

            // 2. 해당 종목에 관심 있는 사용자 조회
            List<Member> members = memberFavoriteStockRepository.findMembersByStock(stock).orElseThrow(() -> new IllegalArgumentException("종목에 관심한 사용자이 존재하지 않습니다."));
            for (Member member : members) {
                // 3. 사용자의 알림 세팅 조회
                NotificationSetting setting = notificationSettingRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("알림 세팅이 존재하지 않습니다."));

                boolean sendNotification = false;

                // 4. impactScore의 부호에 따라 good/bad 분류
                if (impactScore > 0.5) {
                    if (setting.getGoodNews() &&
                            BigDecimal.valueOf(impactScore).compareTo(setting.getGoodSensitivity1()) >= 0) {
                        sendNotification = true;
                    }
                } else if (impactScore < -0.5) {
                    if (setting.getBadNews() &&
                            BigDecimal.valueOf(Math.abs(impactScore)).compareTo(setting.getBadSensitivity1()) >= 0) {
                        sendNotification = true;
                    }
                } else {
                    if (setting.getNeutralNews()) {
                        sendNotification = true;
                    }
                }

                if (sendNotification) {
                    // 5. FCM 토큰 조회
                    FcmToken fcmToken = fcmTokenRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("토큰이 존재하지 않습니다.")); // TODO: 구현 필요

                    // 6. FCM 백엔드로 메시지 전송
                    String title = new StringBuilder()
                            .append("StockPulse에서 보낸 알림입니다")
                            .append(" - ")
                            .append(impact.getStock().getName())
                            .append("의 오늘 등락률")
                            .append(impactScore > 0 ? "(긍정신호)" : "(부정신호)")
                            .toString();

                    String body = new StringBuilder()
                            .append(impact.getNews().getTitle())
                            .toString();

                    fcmClient.sendNotification(fcmToken.getFcmToken(), title, body); // TODO: 구현 필요
                }
            }
        }
    }
}
