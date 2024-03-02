package com.oauth2.login.authentication.infra.kakao;

import com.oauth2.login.authentication.domain.oauth.OAuthApiClient;
import com.oauth2.login.authentication.domain.oauth.OAuthInfoResponse;
import com.oauth2.login.authentication.domain.oauth.OAuthLoginParams;
import com.oauth2.login.authentication.domain.oauth.OAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Component
@RequiredArgsConstructor // final이 붙은 초기화 되지 않은 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성해준다.
public class KakaoApiClient implements OAuthApiClient {

    // GRANT_TYPE의 경우 저렇게 보내라고 정해줬음
    private static final String GRANT_TYPE = "authorization_code";

    @Value("${oauth.kakao.url.auth}") // @Value는 설정파일에서 설정한 내용을 주입시켜주는 어노테이션 (.properties 혹은 .yml)
    private String authUrl;

    @Value("${oauth.kakao.url.api}")
    private String apiUrl;

    @Value("${oauth.kakao.client-id}")
    private String clientId;


    private final RestTemplate restTemplate;

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public String requestAccessToken(OAuthLoginParams params) {
        String url = authUrl + "/oauth/token";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = params.makeBody();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", clientId);

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        KakaoTokens response = restTemplate.postForObject(url, request, KakaoTokens.class);

        assert  response != null;
        return  response.getAccessToken();
    }

    @Override
    public OAuthInfoResponse requestQauthInfo(String accessToken) {
        String url = apiUrl + "/v2/user/me";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "Bearer" + accessToken);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("property_keys", "[\"kakao_account.email\", \"kakao_account.profile\"]");

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        return  restTemplate.postForObject(url, request, KakaoInfoResponse.class);
    }
}
