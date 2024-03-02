package com.oauth2.login.authentication.domain.oauth;


// 회원이 무엇으로 로그인 했는지 타입을 저장하는 Enum Class
// KAKAO와 NAVER 모두 Oauth 인증 제공자임으로 클래스 이름을 OauthProvider라고 했습니다.
public enum OAuthProvider {
    KAKAO, NAVER
}
