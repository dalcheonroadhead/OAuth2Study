package com.oauth2.login.member.domain;

import com.oauth2.login.authentication.domain.oauth.OAuthProvider;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
*  회원 정보를 담는 Member 엔티티
*  Email, Nickname과 같은 프로필 정보나 인증을 어디서 받는지 그 회사를 타입으로 가지고 있스빈다ㅏ.
* */

@Getter // 롬복으로 Getter는 만들겠다.
@Entity // 해당 클래스의 객체 매핑은 JPA에서 담당하고, JPA가 알아서 DB 속의 테이블과 매핑하겠다.
@NoArgsConstructor // 우리는 생성자 없이 간다.
public class Member {
    @Id // id란 변수를 Primary key로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 데이터 베이스에게 위임(== Auto_Increment 하겠다.)
    private Long id;

    private String email;

    private String nickname;

    private OAuthProvider oAuthProvider;

    @Builder
    public Member(String email, String nickname, OAuthProvider oAuthProvider) {
        this.email = email;
        this.nickname = nickname;
        this.oAuthProvider = oAuthProvider;
    }

}
