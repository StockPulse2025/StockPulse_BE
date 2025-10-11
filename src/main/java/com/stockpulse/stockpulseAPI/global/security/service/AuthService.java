package com.stockpulse.stockpulseAPI.global.security.service;

import com.stockpulse.stockpulseAPI.domain.member.entity.Member;
import com.stockpulse.stockpulseAPI.domain.member.repository.MemberRepository;
import com.stockpulse.stockpulseAPI.domain.token.domain.Token;
import com.stockpulse.stockpulseAPI.domain.token.repository.TokenRepository;
import com.stockpulse.stockpulseAPI.global.apiPayload.code.status.ErrorStatus;
import com.stockpulse.stockpulseAPI.global.security.authDTO.AuthResponseDTO;
import com.stockpulse.stockpulseAPI.global.security.authDTO.KakaoProfile;
import com.stockpulse.stockpulseAPI.global.security.authDTO.OAuthToken;
import com.stockpulse.stockpulseAPI.global.security.converter.AuthConverter;
import com.stockpulse.stockpulseAPI.global.security.exception.AuthException;
import com.stockpulse.stockpulseAPI.global.security.provider.JwtTokenProvider;
import com.stockpulse.stockpulseAPI.global.security.provider.KakaoAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final KakaoAuthProvider kakaoAuthProvider;
    private final JwtTokenProvider jwtTokenProvider;

    //카카오 로그인 test
    @Transactional
    public AuthResponseDTO.OAuthResponse kakaoLoginTest(String code) {
        OAuthToken oAuthToken = getKakaoOauthToken(code);

        KakaoProfile kakaoProfile;
        try {
            kakaoProfile =
                    kakaoAuthProvider.requestKakaoProfile(oAuthToken.getAccess_token());
        } catch (Exception e) {
            throw new AuthException(ErrorStatus.INVALID_REQUEST_INFO_KAKAO);
        }

        // 유저 정보 받기
        Optional<Member> queryMember =
                memberRepository.findByEmail(
                        kakaoProfile.getKakaoAccount().getEmail());

        // 가입자 혹은 비가입자 체크해서 로그인 처리
        if (queryMember.isPresent()) {
            Member member = queryMember.get();
            return getOauthResponseForPresentUser(member, queryMember);
        }

        Member member = memberRepository.save(AuthConverter.kakaoToMember(kakaoProfile));
        return getOauthResponseForNewUser(member);
    }

    //카카오 로그인
    @Transactional
    public AuthResponseDTO.OAuthResponse kakaoLogin(String token) {

        KakaoProfile kakaoProfile;
        try {
            kakaoProfile =
                    kakaoAuthProvider.requestKakaoProfile(token);
        } catch (Exception e) {
            throw new AuthException(ErrorStatus.INVALID_REQUEST_INFO_KAKAO);
        }
        // 유저 정보 받기
        Optional<Member> queryMember =
                memberRepository.findByEmail(
                        kakaoProfile.getKakaoAccount().getEmail());
        // 가입자 혹은 비가입자 체크해서 로그인 처리
        if (queryMember.isPresent()) {
            Member member = queryMember.get();
            return getOauthResponseForPresentUser(member, queryMember);
        }

        Member member = memberRepository.save(AuthConverter.kakaoToMember(kakaoProfile));
        return getOauthResponseForNewUser(member);
    }

    private AuthResponseDTO.OAuthResponse getOauthResponseForNewUser(Member member) {
        String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        saveNewMember(refreshToken, member);
        return AuthConverter.toOAuthResponse(accessToken, refreshToken, member);
    }

    private AuthResponseDTO.OAuthResponse getOauthResponseForPresentUser(Member member, Optional<Member> queryMember) {
        String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        Optional<Token> savedRefreshToken = tokenRepository.findByMemberId(member.getId());
        if (savedRefreshToken.isPresent()) {
            savedRefreshToken.get().changeToken(refreshToken);
        } else {
            tokenRepository.save(new Token(refreshToken, member.getId()));
        }
        return AuthConverter.toOAuthResponse(accessToken, refreshToken, queryMember.get());
    }

    private void saveNewMember(String refreshToken, Member member) {
        tokenRepository.save(new Token(refreshToken, member.getId()));
        memberRepository.save(member);
    }

    private OAuthToken getKakaoOauthToken(String code) {
        OAuthToken oAuthToken;
        try {
            oAuthToken = kakaoAuthProvider.requestToken(code);
        } catch (Exception e) {
            throw new AuthException(ErrorStatus.AUTH_INVALID_CODE);
        }
        return oAuthToken;
    }
}
