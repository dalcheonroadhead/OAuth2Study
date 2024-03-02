package com.oauth2.login.authentication.infra.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

// https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token-response
// 를 참조하여 응답으로 들어오는 값들에 대해 전부 명세하였다. 이 클래스는 Model로 쓰인다.

@Getter
@NoArgsConstructor
public class KakaoTokens {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private String expiresIn;

    @JsonProperty("refresh_token_expires_in")
    private String refreshTokenExpiresIn;

    @JsonProperty("scope")
    private String scope;


}
