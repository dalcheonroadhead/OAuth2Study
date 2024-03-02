package com.oauth2.login.authentication.infra.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oauth2.login.authentication.domain.oauth.OAuthInfoResponse;
import com.oauth2.login.authentication.domain.oauth.OAuthProvider;
import lombok.Getter;


// https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info
// 이거 토대로 받아왔습니다.
@Getter
// JSON으로 읽어온 데이터를 객체에 매핑하는 과정(역직렬화)에서
// 객체에는 알 수 없는 property가 JSON 데이터에 있어도 에러를 내뱉지 않고 무시해주는 어노테이션이다.
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoInfoResponse implements OAuthInfoResponse {

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class KakaoAccount {
        private KakaoProfile profile;
        private String email;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class KakaoProfile {
        private String nickname;
    }


    @Override
    public String getEmail() {
        return kakaoAccount.email;
    }

    @Override
    public String getNickName() {
        return kakaoAccount.profile.nickname;
    }

    @Override
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.KAKAO;
    }
}
