package com.stockpulse.stockpulseAPI.global.security.authDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class AuthRequestDTO {
    @Getter
    public static class RefreshTokenDTO {
        @JsonProperty("refresh_token")
        String refreshToken;
    }
}
