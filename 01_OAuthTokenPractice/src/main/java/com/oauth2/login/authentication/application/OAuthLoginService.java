package com.oauth2.login.authentication.application;

import com.oauth2.login.authentication.domain.AuthTokens;
import com.oauth2.login.authentication.domain.AuthTokensGenerator;
import com.oauth2.login.authentication.domain.oauth.OAuthInfoResponse;
import com.oauth2.login.authentication.domain.oauth.OAuthLoginParams;
import com.oauth2.login.authentication.domain.oauth.RequestOAuthInfoService;
import com.oauth2.login.member.domain.Member;
import com.oauth2.login.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {
    private final MemberRepository memberRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final RequestOAuthInfoService requestOAuthInfoService;


    public AuthTokens login (OAuthLoginParams params) {

        // 카카오/ 네이버 같은 OAuth 플랫폼에서 모든 절차 거친 후 프로필 정보 가져오기
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
        // email 정보로 사용자 확인 (없으면 새로 가입 처리)
        Long memberId = findOrCreateMember(oAuthInfoResponse);
        // AccessToken 생성 후 프론트로 보내기
        return authTokensGenerator.generate(memberId);
    }

    private Long findOrCreateMember(OAuthInfoResponse oAuthInfoResponse){
        return memberRepository.findByEmail(oAuthInfoResponse.getEmail())
                .map(Member::getId)
                .orElseGet(() -> newMember(oAuthInfoResponse));

    }

    private Long newMember(OAuthInfoResponse oAuthInfoResponse){
        Member member = Member.builder()
                .email(oAuthInfoResponse.getEmail())
                .nickname(oAuthInfoResponse.getNickName())
                .oAuthProvider(oAuthInfoResponse.getOAuthProvider())
                .build();

        return  memberRepository.save(member).getId();
    }
}
