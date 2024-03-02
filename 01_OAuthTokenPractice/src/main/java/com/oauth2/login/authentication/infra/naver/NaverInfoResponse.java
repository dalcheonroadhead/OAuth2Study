package com.oauth2.login.authentication.infra.naver;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oauth2.login.authentication.domain.oauth.OAuthInfoResponse;
import com.oauth2.login.authentication.domain.oauth.OAuthProvider;
import lombok.Getter;

//https://developers.naver.com/docs/login/profile/profile.md 해당 명세를 보고 작성했습니다.
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverInfoResponse implements OAuthInfoResponse {

    @JsonProperty("response")
    private Response response;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Response {
        private String email;
        private String nickname;

    }

    @Override
    public String getEmail() {
        return response.email;
    }

    @Override
    public String getNickName() {
        return response.nickname;
    }

    @Override
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.NAVER;
    }
}
