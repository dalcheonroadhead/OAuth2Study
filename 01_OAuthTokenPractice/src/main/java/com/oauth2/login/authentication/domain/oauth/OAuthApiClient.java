package com.oauth2.login.authentication.domain.oauth;


//Oauth 요청의 전 과정을 가지고 있는 Client 인터페이스 이다.
// 전과정이란?
    // 2번째 함수인 requestAccessToken 함수의 경우, 인가코드를 기반으로 인증 API를 요청해서 Access Token을 획득한다.
    // 3번째 함수인 requestQauthInfo는 Access Token을 기반으로 Email이나 Nickname이 포함된 프로필을 획득한다.
public interface OAuthApiClient {
    OAuthProvider oAuthProvider();
    String requestAccessToken(OAuthLoginParams params);
    OAuthInfoResponse requestQauthInfo(String accessToken);
}
