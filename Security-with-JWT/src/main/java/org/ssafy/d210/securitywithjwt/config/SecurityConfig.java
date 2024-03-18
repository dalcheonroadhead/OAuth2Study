package org.ssafy.d210.securitywithjwt.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.JeeConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.ssafy.d210.securitywithjwt.jwt.*;

/*
* JWT 세팅
*
* */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    @Bean
    // 1) 비밀번호를 안전하게 저장할 수 있도록 비밀번호의 단방향 암호화를 지원하는 인터페이스
    // 우리는 해당 인터페이스의 구현체 중 하나인 BCryptPasswordEncoder를 사용한다.
    // 해당 함수는 Bcrypt라는 해시 함수를 사용해 비밀번호를 암호화 한다.
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean

    /* 2) SpringSecurity에서 제공하는 인증, 인가를 위한 필터들의 모음
    *     가장 핵심이 되는 기능을 제공, 거의 대부분의 Security 서비스는 이 Filter Chain에서 실행됨
    *     개발 취지에 따라 기본 제공되는 필터들 사이에 커스텀 필터를 넣어도 된다.
    */
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 2-1) 사이트간 위조 요청 공격인 Cross Site Request Forgery에 대한 예방 기능을 끈다.
                // 이 좋은 기능을 왜 끌까?
                // 스프링 공식 문서를 읽어보면, 해당 공격은 일반 사용자가 브라우저를 사용할 때,
                // 해커에 의해 미리 설정된 행동을 자신의 의지와 상관없이 하게 되면서 벌어지는 공격이다.
                // 따라서 REST API로만 Spring을 쓸 경우 (즉 일반 사용자와 대면할 일이 없는 경우)
                // 굳이 필요가 없는 기능이다.
                .csrf(AbstractHttpConfigurer::disable)


                // 2-2) 세션 인증 방식을 사용하지 않는다.
                // -> 따라서 Session 상태 유지가 필요없기 때문에 STATELESS로 변경
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 2-3) exceptionHandling 인증/인가에서 Error 발생시 어떻게 후처리 할 것인지 설정
                // 구현방법: autheticationEntryPoint는 사용자 인증에서 예외가 발생했을 시 그것을 어떻게 처리할 것인가를 정의한 클래스
                .exceptionHandling(autheticationManager -> autheticationManager
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 인증 실패 시
                        .accessDeniedHandler(accessDeniedHandler)) // 권한이 없을 경우

                // 2-4) iframe을 이용한 디도스 공격을 막기 위해
                //      iframe 태그로 보여줄 수 있는 것은 같은 origin(domain)을 가진 사이트 페이지 만으로 한정한다.
                .headers((headers) ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

                // 2-5) 세션 인증 방식이 아닌 JWT 토큰 인증 방식을 사용하므로, Session은 STATELESS로 설정
                .sessionManagement((session)->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        (authorizeRequest) ->
                                authorizeRequest
                                        .requestMatchers("/api/**").permitAll()   // 로그인 API 는 인증 필요없이 그냥 들어와라// 회원가입 API 또한 인증 필요없이 그냥 들어와라
                                        .requestMatchers(PathRequest.toH2Console()).permitAll() // h2-console, favicon.ico 요청 인증 무시
                                        .anyRequest().authenticated()
                )

                // 2-6) apply deprecated 됨 대신 바로 addFilterBefore을 쓴다.
                //      addFilterBefore은 해당 필터체인 전에 실행된다.
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

                // http 필터
                return  http.build();



    }

}

/*
* X-Frame-Options
* : Http Response Header의 하나로 브라우저가
*   해당 사이트를 <frame> <iframe> <embed> <object> 태그 내부에서 랜더링 해도 되는지
*
*
* */
