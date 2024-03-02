package com.oauth2.login.authentication.domain.oauth;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// 지금까지 만든 OAuthApiClient 구현체들을 사용하는 service 클래스
// KakaoApiClient와 NaverApiClient를 직접 주입 받아서 사용하면 중복되는 코드가 많아지지만 List<OAuthApiClient>를
// 주입 받아서 Map으로 만들어두면 편하게 하나씩 사용 가능
// List<인터페이스>를 주입 받으면 해당 인터페이스의 구현체들이 전부 list에 담겨 온다.
@Component
public class RequestOAuthInfoService {
    private final Map<OAuthProvider, OAuthApiClient> clients;

    // 이게 clients를 초기화 해주는 매소드
    // 해당 List 안에 전부 담겨 있다.
    public RequestOAuthInfoService(List<OAuthApiClient> clients) {
        this.clients = clients.stream().collect(
                Collectors.toUnmodifiableMap(OAuthApiClient::oAuthProvider, Function.identity())
        );
    }

    public OAuthInfoResponse request (OAuthLoginParams params) {

        // 카카오 네이버 중 하나의 클라이언트 특정
        OAuthApiClient client = clients.get(params.oAuthProvider());

        // 특정한 외부 API로 인가 코드 보내고 AT 받아오기
        String accessToken = client.requestAccessToken(params);

        // AT 받은 걸로 사용자 정보 요청하고 그 결과를 반환
        return client.requestQauthInfo(accessToken);
    }

}
