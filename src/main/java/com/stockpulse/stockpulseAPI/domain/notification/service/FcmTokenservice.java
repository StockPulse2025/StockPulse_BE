package com.stockpulse.stockpulseAPI.domain.notification.service;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.member.repository.MemberRepository;
import com.stockpulse.stockpulseAPI.domain.notification.dto.FcmTokenRequestDto;
import com.stockpulse.stockpulseAPI.domain.notification.entity.FcmToken;
import com.stockpulse.stockpulseAPI.domain.notification.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmTokenservice {
    private final FcmTokenRepository fcmTokenRepository;
    private final MemberRepository memberRepository;

    public void registerFcmToken(Long userId, FcmTokenRequestDto dto) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 기존 토큰 보유 여부 확인해서 있으면 업데이트, 없으면 새로 저장
        FcmToken fcmToken = fcmTokenRepository.findByMember(member)
                .orElse(null); // 생성자 추가해줘야 함

        if (fcmToken == null) {
            // 최초 등록
            fcmToken = FcmToken.builder()
                    .member(member)
                    .fcmToken(dto.getFcmToken())
                    .build();

            fcmTokenRepository.save(fcmToken);
            return;
        } else if (!fcmToken.getFcmToken().equals(dto.getFcmToken())) {
            // 토큰 변경 시에만 업데이트
            fcmToken.setFcmToken(dto.getFcmToken());
            fcmTokenRepository.save(fcmToken);
        }
    }
}
