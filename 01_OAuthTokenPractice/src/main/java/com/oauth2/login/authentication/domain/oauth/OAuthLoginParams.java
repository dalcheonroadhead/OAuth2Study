package com.oauth2.login.authentication.domain.oauth;

import org.springframework.util.MultiValueMap;

// 카카오, 네이버 OAuth 요청에 필요한 파라미터 값들을 가지고 있는 인터페이스
// 이 인터페이스의 구현체는 Controller의 @RequestBody로도 사용되기 때문에, getXXX라는 네이밍이 사용되지 않아야 한다.
public interface OAuthLoginParams {


    OAuthProvider oAuthProvider();
    MultiValueMap<String, String> makeBody();
}
