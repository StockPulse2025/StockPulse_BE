package com.stockpulse.stockpulseAPI.domain.notification.service;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.news.entity.NewsImpact;
import com.stockpulse.stockpulseAPI.domain.notification.entity.NotificationSetting;
import com.stockpulse.stockpulseAPI.domain.notification.repository.FcmTokenRepository;
import com.stockpulse.stockpulseAPI.domain.stock.entity.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final FcmTokenRepository fcmTokenRepository;
    private final MemberInterestRepository memberInterestRepository;
    private final FcmClient fcmClient;


    public void notification() {
        /* TODO
         * 업데이트된 뉴스 영향도 가져오기
         * 영향도로 들어온 종목에 관심 있음을 등록한 사용자들을 찾는다.
         * 사용자들마다 알림 세팅을 조회하여 알림을 만족하는지 판단.
         * 해당 영향도 정보가 +, - 와 절대값을 가지고 사용자들의 알림 필터링 조건을 만족하는 판단.
         * 조건을 만족하는 사용자들의 FCM토큰을 조회 후, FCM 백엔드로 해당 영향도 관련 뉴스 내용 전송
         * */

        // TODO : 실제로는 업데이트된 뉴스들을 DB로부터 가져와야 함.
        // List<NewsImpact> newsImpacts = newsImpactRepository.findRecentlyUpdated(); // TODO: 구현 필요
        List<NewsImpact> newsImpacts = List.of(
                new NewsImpact(),
                new NewsImpact());

        for (NewsImpact newsImpact : newsImpacts) {
            Stock stock = newsImpact.getStock();
            double impactScore = newsImpact.getImpactRate().doubleValue(); // ex: +5.3, -2.1

            // 2. 해당 종목에 관심 있는 사용자 조회
            List<Member> members = memberInterestRepository.findMembersByStock(stock); // TODO: 구현 필요

            for (Member member : members) {
                // 3. 사용자의 알림 세팅 조회
                NotificationSetting setting = notificationSettingRepository.findByMember(member);

                // 4. 알림 조건 만족 여부 판단
                if (impactScore )

                if (Math.abs(impactScore) < setting.getB()) continue;
                if (impactScore > 0 && !setting.isNotifyOnRise()) continue;
                if (impactScore < 0 && !setting.isNotifyOnFall()) continue;


            }
        }
    }
}
