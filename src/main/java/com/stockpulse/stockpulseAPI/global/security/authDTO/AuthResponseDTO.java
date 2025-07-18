package com.stockpulse.stockpulseAPI.global.security.authDTO;

import lombok.*;

public class AuthResponseDTO {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class OAuthResponse {
        Long memberId;
        String accessToken;
        String refreshToken;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class TokenResponse {
        String accessToken;
        String refreshToken;
    }

}
