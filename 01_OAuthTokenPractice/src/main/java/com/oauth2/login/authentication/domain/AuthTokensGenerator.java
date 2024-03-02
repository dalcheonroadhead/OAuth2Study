package com.oauth2.login.authentication.domain;

import com.oauth2.login.authentication.infra.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;


// AuthToken을 발급해주는 클래스
// AuthToken은 OAuth 서비스 각각의 AT RT에 의존하지 않고,
// 우리 서비스에서 직접 만든 토큰이다.
// 우리는 이 토큰 AT,RT 만료를 기준으로 일괄적으로 네이버 카카오 AT, RT를 통제한다.
// 각 외부 API마다 Token 만료 기간이 다른데, 이를 따로 관리하지 않기 위함이고, 외부 API 토큰 탈취에 대비하기 위함이다.
@Component
@RequiredArgsConstructor
public class AuthTokensGenerator {
    private static final  String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일

    private final JwtTokenProvider jwtTokenProvider;

    // 사용자 식별값인 memberId를 받아서 Access Token 생성
    public AuthTokens generate(Long memberId) {
        long now = (new Date()).getTime();

        Date accessTokenExpiredAt = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiredAt = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        String subject = memberId.toString();
        String accessToken = jwtTokenProvider.generate(subject, accessTokenExpiredAt);
        String refreshToken = jwtTokenProvider.generate(subject, refreshTokenExpiredAt);

        return AuthTokens.of(accessToken, refreshToken, BEARER_TYPE, ACCESS_TOKEN_EXPIRE_TIME /1000L);
    }

    //AT에서 사용자 식별 값인 memberId를 추출하는 함수
    public Long extractMemberId(String accessToken) {
        return  Long.valueOf(jwtTokenProvider.extractSubject(accessToken));
    }
}
