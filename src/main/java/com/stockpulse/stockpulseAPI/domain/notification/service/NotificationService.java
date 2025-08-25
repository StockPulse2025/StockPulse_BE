package com.stockpulse.stockpulseAPI.domain.notification.service;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.member.repository.MemberRepository;
import com.stockpulse.stockpulseAPI.domain.news.entity.Impact;
import com.stockpulse.stockpulseAPI.domain.news.repository.ImpactRepository;
import com.stockpulse.stockpulseAPI.domain.notification.dto.NotificationResponseDTO;
import com.stockpulse.stockpulseAPI.domain.notification.dto.NotificationSettingRequestDTO;
import com.stockpulse.stockpulseAPI.domain.notification.dto.NotificationSettingResponseDTO;
import com.stockpulse.stockpulseAPI.domain.notification.entity.FcmToken;
import com.stockpulse.stockpulseAPI.domain.notification.entity.Notification;
import com.stockpulse.stockpulseAPI.domain.notification.entity.NotificationSetting;
import com.stockpulse.stockpulseAPI.domain.notification.fcm.FcmClient;
import com.stockpulse.stockpulseAPI.domain.notification.repository.FcmTokenRepository;
import com.stockpulse.stockpulseAPI.domain.notification.repository.NotificationRepository;
import com.stockpulse.stockpulseAPI.domain.notification.repository.NotificationSettingRepository;
import com.stockpulse.stockpulseAPI.domain.notification.service.event.ImpactSavedEvent;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import com.stockpulse.stockpulseAPI.domain.stock.repository.MemberFavoriteStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final FcmTokenRepository fcmTokenRepository;
    private final FcmClient fcmClient;
    private final NotificationSettingRepository notificationSettingRepository;
    private final NotificationRepository notificationRepository;
    private final MemberFavoriteStockRepository memberFavoriteStockRepository;
    private final ImpactRepository impactRepository;
    private final MemberRepository memberRepository;

    public void initNotification(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Member not found"));
        boolean exists = notificationSettingRepository.existsByMember(member);

        if(!exists) {
            NotificationSetting notificationSetting = NotificationSetting.builder()
                .member(member)
                .interestStock(false)
                .ownStock(false)
                .goodNews(false)
                .badNews(false)
                .neutralNews(false)
                .goodSensitivity1(BigDecimal.valueOf(0.5))
                .goodSensitivity2(BigDecimal.valueOf(2.0))
                .badSensitivity1(BigDecimal.valueOf(0.5))
                .badSensitivity2(BigDecimal.valueOf(2.0))
                .build();
        notificationSettingRepository.save(notificationSetting);
        }
    }

    @Transactional
    public void resetNotification(Long memberId) {
        NotificationSetting notificationSetting = notificationSettingRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("알림 세팅이 존재하지 않습니다."));

        notificationSetting.resetSetting();
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 새로운 트랜잭션을 시작하도록 변경@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    @EventListener
//    @Transactional
    public void handleImpactSavedEvent(ImpactSavedEvent event) {
        notification(event.getImpacts());
    }

    public void notification(List<Impact> impacts) {
        /* TODO
         * 업데이트된 뉴스 영향도 가져오기
         * 영향도로 들어온 종목에 관심 있음을 등록한 사용자들을 찾는다.
         * 사용자들마다 알림 세팅을 조회하여 알림을 만족하는지 판단.
         * 해당 영향도 정보가 +, - 와 절대값을 가지고 사용자들의 알림 필터링 조건을 만족하는 판단.
         * 조건을 만족하는 사용자들의 FCM토큰을 조회 후, FCM 백엔드로 해당 영향도 관련 뉴스 내용 전송
         * */

        // 1. 업데이트된 뉴스 영향도 조회
//        List<NewsImpact> newsImpacts = newsImpactRepository.findRecentlyUpdated(LocalDateTime.now().minusMinutes(10));
//        List<Impact> impacts = impactRepository.findAll();

        for (Impact impact : impacts) {
            Stock stock = impact.getStock();
            Double impactScore = impact.getImpactRate().doubleValue(); // ex: +5.3, -2.1

            // 2. 해당 종목에 관심 있는 사용자 조회
            List<Member> members = memberFavoriteStockRepository
                    .findMembersOwnStockOrMemberFavoriteStockByStockId(stock)
                    .orElseThrow(() -> new IllegalArgumentException("종목에 관심한 사용가 존재하지 않습니다."));
            for (Member member : members) {
                // 3. 사용자의 알림 세팅 조회
                NotificationSetting setting
                        = notificationSettingRepository.findByMember(member).orElseThrow(()
                        -> new IllegalArgumentException("알림 세팅이 존재하지 않습니다."));

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

                    Notification notification = new Notification(impact, member);
                    notificationRepository.save(notification);

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

                    fcmClient.sendNotification(fcmToken.getFcmToken(), title, body);
                }
            }
        }
    }

    public NotificationSettingResponseDTO getNotificationSetting(Long userId) {
        NotificationSetting notificationSetting = notificationSettingRepository.findByMemberId(userId).orElseThrow(() -> new IllegalArgumentException("알림 세팅이 존재하지 않습니다."));
        return new NotificationSettingResponseDTO().toResponseDTO(notificationSetting);
    }

    public NotificationResponseDTO getNotificationHistory(Long userId, String type) {
        List<Notification> notifications = null;
        switch (type) {
            case "interest":
                notifications = notificationRepository.findFavoriteStockHistoryByMemberId(userId)
                        .orElseThrow(() -> new IllegalArgumentException("사용자가 관심 종목이 없습니다."));
                break;
            case "owned":
                notifications = notificationRepository.findOwnStockHistoryByMemberId(userId)
                        .orElseThrow(() -> new IllegalArgumentException("사용자가 보유한 종목이 없습니다."));
                break;
            default:
                throw new IllegalArgumentException("작업 타입이 지정되지 않음");
        }
        return new NotificationResponseDTO().toResponseDTO(notifications);
    }

    @Transactional
    public void updateNotificationSetting(Long memberId, NotificationSettingRequestDTO.UpdateDTO request) {
        NotificationSetting setting = notificationSettingRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("알림 세팅이 존재하지 않습니다."));
        setting.updateSettings(request);
    }
}
