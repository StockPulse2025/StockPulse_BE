package com.stockpulse.stockpulseAPI.global.apiPayload.code.status;

import com.stockpulse.stockpulseAPI.global.apiPayload.code.BaseCode;
import com.stockpulse.stockpulseAPI.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),

    // 회원 가능 관련
    USER_LOGIN_OK(HttpStatus.OK, "AUTH2001", "회원 로그인이 완료되었습니다."),
    USER_LOGOUT_OK(HttpStatus.OK, "AUTH2002", "회원 로그아웃이 완료되었습니다."),
    USER_DELETE_OK(HttpStatus.OK, "AUTH2003", "회원 탈퇴가 완료되었습니다."),
    USER_REFRESH_OK(HttpStatus.OK, "AUTH2004", "토큰 재발급이 완료되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build();
    }
}
