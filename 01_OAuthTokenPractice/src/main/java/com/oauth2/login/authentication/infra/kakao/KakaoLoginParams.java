package com.oauth2.login.authentication.infra.kakao;

import com.oauth2.login.authentication.domain.oauth.OAuthLoginParams;
import com.oauth2.login.authentication.domain.oauth.OAuthProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@NoArgsConstructor // 매개 변수를 갖지 않는 기본 생성자를 생성해준다.
public class KakaoLoginParams implements OAuthLoginParams {

    private String authorizationCode;
    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        return null;
    }
}
