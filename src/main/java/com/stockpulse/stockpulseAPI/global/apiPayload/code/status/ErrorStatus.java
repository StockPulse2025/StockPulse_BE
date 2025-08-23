package com.stockpulse.stockpulseAPI.global.apiPayload.code.status;

import com.stockpulse.stockpulseAPI.global.apiPayload.code.BaseErrorCode;
import com.stockpulse.stockpulseAPI.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수 입니다."),

    // Auth 관련
    AUTH_EXTRACT_ERROR_TEST(HttpStatus.UNAUTHORIZED, "AUTH_000TSET", "토큰 추출에 실패했습니다TEST."),

    AUTH_EXTRACT_ERROR(HttpStatus.UNAUTHORIZED, "AUTH_000", "토큰 추출에 실패했습니다."),
    AUTH_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_001", "토큰이 만료되었습니다."),
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_002", "토큰이 유효하지 않습니다."),
    INVALID_LOGIN_REQUEST(HttpStatus.UNAUTHORIZED, "AUTH_003", "올바른 아이디나 패스워드가 아닙니다."),
    NOT_EQUAL_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_004", "리프레시 토큰이 다릅니다."),
    NOT_CONTAIN_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_005", "해당하는 토큰이 저장되어있지 않습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH_006", "비밀번호가 일치하지 않습니다."),
    INVALID_REQUEST_INFO_KAKAO(HttpStatus.UNAUTHORIZED, "AUTH_007", "카카오 정보 불러오기에 실패하였습니다."),
    AUTH_INVALID_CODE(HttpStatus.UNAUTHORIZED, "", "코드가 유효하지 않습니다."),

    // User 관련
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "존재하지 않는 사용자입니다."),
    USER_EXISTS(HttpStatus.BAD_REQUEST, "USER_002", "이미 존재하는 아이디입니다."),
    USER_DELETE_FAILED(HttpStatus.NOT_FOUND, "USER_003", "회원 탈퇴에 실패했습니다."),

    // Stock 관련
    STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "STOCK_001", "존재하지 않는 종목입니다."),

    // News 관련
    NEWS_NOT_FOUND(HttpStatus.NOT_FOUND, "NEWS_001", "존재하지 않는 뉴스입니다."),
    NEWS_CONTENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "NEWS_002", "뉴스 본문이 존재하지 않습니다."),
    GEMINI_NOT_WORK(HttpStatus.INTERNAL_SERVER_ERROR, "NEWS_003", "AI 요약 서비스에 오류가 발생했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
