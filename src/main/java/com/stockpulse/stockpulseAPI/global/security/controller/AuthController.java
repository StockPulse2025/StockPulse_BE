package com.stockpulse.stockpulseAPI.global.security.controller;

import com.stockpulse.stockpulseAPI.domain.token.service.TokenService;
import com.stockpulse.stockpulseAPI.global.apiPayload.ApiResponse;
import com.stockpulse.stockpulseAPI.global.apiPayload.code.status.SuccessStatus;
import com.stockpulse.stockpulseAPI.global.security.authDTO.AuthResponseDTO;
import com.stockpulse.stockpulseAPI.global.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @ResponseStatus(code = HttpStatus.OK)
    @Operation(
            summary = "카카오 로그인 API (인가 코드 → JWT 발급)",
            description = "회원가입과 로그인을 한번에 처리하는 카카오 로그인 API 입니다. 인가코드를 전달하면 JWT 토큰이 발급됩니다.")
    @GetMapping("/kakao")
    public ApiResponse<AuthResponseDTO.OAuthResponse> kakaoLoginTest(@RequestParam("code") String code) {
        return ApiResponse.of(SuccessStatus.USER_LOGIN_OK, authService.kakaoLoginTest(code));
    }

/*    @ResponseStatus(code = HttpStatus.OK)
    @Operation(
            summary = "카카오 로그인 API (Access Token → JWT 발급)",
            description = "카카오 Access Token을 전달하면 JWT 토큰이 발급됩니다.")
    @GetMapping("/login/kakao")
    public ApiResponse<AuthResponseDTO.OAuthResponse> kakaoLogin(@RequestParam("access_token") String token) {
        return ApiResponse.of(SuccessStatus.USER_LOGIN_OK, authService.kakaoLogin(token));
    }*/

    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "회원 탈퇴 API", description = "회원 탈퇴 API 입니다")
    @DeleteMapping("/deactivate")
    public ApiResponse<Void> AccountDeactivation(HttpServletRequest request) {
        tokenService.deleteUser(request);
        return ApiResponse.of(SuccessStatus.USER_DELETE_OK,null);
    }

/*    @Operation(summary = "사용자 ID 주입 테스트", description = "@AuthUser을 사용한 현사용자 ID 자동 주입 예시입니다.")
    @GetMapping("test")
    public ApiResponse<String> loginTest(@AuthUser Long userId){
        String result = "userID : " + userId;
        return ApiResponse.onSuccess(result);
    }*/
}