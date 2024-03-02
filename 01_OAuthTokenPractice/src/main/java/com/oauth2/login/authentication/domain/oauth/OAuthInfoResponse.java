package com.oauth2.login.authentication.domain.oauth;


// AccessToken을 통해 요청을 보낸 후, 그 요청이 성공하면 Naver나 Kakao에서 사용자 정보를 토대로 응답값을 보낼 것이다.
// 해당 인터페이스는 그 응답값들을 우리 서비스의 Model로 변환 시키기 위한 인터페이스 이다.

public interface OAuthInfoResponse {
    String getEmail();
    String getNickName();
    OAuthProvider getOAuthProvider();

}
