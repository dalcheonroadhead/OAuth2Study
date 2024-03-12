package com.ssafy.oauth.oauthtest.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final Environment env;

    // 0) Client로서의 역할을 하기 위한 RestTemplate
    private final RestTemplate restTemplate = new RestTemplate();

    // 1) 로그인 프로세스 -> 인가코드로 Access Token을 얻고, Access Token으로 필요한 값을 빼내서 온다.
    public void socialLogin(String code, String registrationId) {

        // (2) AT를 받는 로직, (3) User 정보를 얻는 로직
        String accessToken = getAccessToken(code, registrationId);
        JsonNode userResourceNode = getUserResource(accessToken, registrationId);


        System.out.println("userResourceNode = " + userResourceNode);

        String id = userResourceNode.get("id").asText();
        String email = userResourceNode.get("email").asText();
        String nickname = userResourceNode.get("name").asText();
        System.out.println("id = " + id);
        System.out.println("email = " + email);
        System.out.println("nickname = " + nickname);
    }

    // 2) 인가코드를 주고 AccessToken 받는 매소드
    private String getAccessToken(String authorizationCode, String registrationId) {

        // 2-1) 각 OAuth2 API 별 Client id, Client-secret을 가져오기
        String clientId = env.getProperty("oauth2." + registrationId + ".client-id");
        String clientSecret = env.getProperty("oauth2."+registrationId +".client-secret");
        String redirectUri = env.getProperty("oauth2." + registrationId + ".redirect-uri");
        String tokenUri = env.getProperty("oauth2." + registrationId + ".token-uri");

        // 2-2) Access Token을 가져오기 위한 Request Params 세팅
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        // 2-3) Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 2-4) Body와 Header를 넣기
        HttpEntity entity = new HttpEntity(params, headers);

        // JsonNode == json tree 구조를 가진 객체
        // ResponseEntity == HttpEntity의 확장 클래스 -> HttpStatus 상태 코드를 추가한 전체 Http 응답 표현 클래스
        ResponseEntity<JsonNode> responseNode = restTemplate.exchange(tokenUri, HttpMethod.POST, entity, JsonNode.class);

        // 2-5) 요청을 보낸 뒤에 받은 body를 뜯어보고, 그거의 key 이름이 "access_token"인 녀석을 text형태로 바꾸어서 넣는다.
        JsonNode accessTokenNode = responseNode.getBody();
        return accessTokenNode.get("access_token").asText();
    }

    // 3) 얻은 Access Token으로 resource 서버에서 원하는 값을 가져오기
    private JsonNode getUserResource(String accessToken, String registrationId){

        // 3-1) Resource Server는 어디지?
        String resourceUri = env.getProperty("oauth2." + registrationId + ".resource-uri");

        // 3-2) Header에 AccessToken 넣기
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer" + accessToken);

        // 3-3) restTemplate 로 헤더를 넣어서 값을 보내고, 내가 Google API 로 미리 설정한, 값들을 가져온다.
        HttpEntity entity = new HttpEntity(headers);
        return restTemplate.exchange(resourceUri, HttpMethod.GET, entity, JsonNode.class).getBody();
    }


}
