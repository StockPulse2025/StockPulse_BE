package com.stockpulse.stockpulseAPI.global.security.converter;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.global.security.authDTO.AuthResponseDTO;
import com.stockpulse.stockpulseAPI.global.security.authDTO.KakaoProfile;

public class AuthConverter {

    public static Member kakaoToMember(KakaoProfile kakaoProfile) {
        return Member.builder()
                .nickname(kakaoProfile.getKakaoNickname().getNickname())
                .email(kakaoProfile.getKakaoAccount().getEmail())
                .build();
    }

    public static AuthResponseDTO.OAuthResponse toOAuthResponse(
            String accessToken, String refreshToken, Member member) {
        return AuthResponseDTO.OAuthResponse.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .memberId(member.getId())
                .build();
    }

    public static AuthResponseDTO.TokenResponse toTokenRefreshResponse(
            String accessToken, String refreshToken) {
        return AuthResponseDTO.TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
