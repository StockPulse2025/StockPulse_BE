package com.stockpulse.stockpulseAPI.domain.token.controller;

import com.stockpulse.stockpulseAPI.domain.token.service.TokenService;
import com.stockpulse.stockpulseAPI.global.apiPayload.ApiResponse;
import com.stockpulse.stockpulseAPI.global.security.authDTO.AuthResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
@Tag(name = "Token", description = "JWT 토큰 관련 API")
public class TokenController {

    private final TokenService tokenService;

    @Operation(
            summary = "JWT Access Token 재발급 API",
            description = "Refresh Token 을 검증하고 새로운 Access Token 과 Refresh Token 을 응답합니다.")
    @PostMapping("/reissue")
    public ApiResponse<AuthResponseDTO.TokenResponse> reissueToken(final HttpServletRequest request) {
        AuthResponseDTO.TokenResponse tokenResponse = tokenService.reissueToken(request);

        return ApiResponse.onSuccess(tokenResponse);
    } // Access Token 재발급

    @Operation(
            summary = "로그아웃 API",
            description = "Refresh Token을 삭제합니다.")
    @GetMapping("/logout")
    public void logout(final HttpServletRequest request) {
        tokenService.deleteToken(request);
    } // 로그아웃
}
