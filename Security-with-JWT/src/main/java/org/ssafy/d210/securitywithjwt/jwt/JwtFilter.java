package org.ssafy.d210.securitywithjwt.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

/* 검증이 끝난 JWT를 받아와서 해부한 뒤, 유저정보를 꺼낸다.
*   유저정보를 UsernamePasswordAuthenticationFilter에 전달한다.
* */
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final TokenProvider tokenProvider;


    // 1) 실제 필터링 로직
    // 토큰 안의 인증 정보를 SecurityContext 에 저장하는 역할 수행
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        // 1-1) 들어온 요청에 대한 정보 취득을 위한 객체
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        // 1-2) Header에서 Bearer 빼고 토큰만 가져온다.
        String jwt = resolveToken(httpServletRequest);

        // 1-3) 스트링쿼리문과 요청 포트번호를 제외한 컨텍스트 경로 + 서블릿 경로만 가져온다.
        String requestURI = httpServletRequest.getRequestURI();

        if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)){

            // 1-4) jwt token 을 해부해서, 이름, 패스워드, 가진 권한들을 얻어낸다. 그것들을 조합해 Authentication 의 하위 구현 클래스인
            //      UsernamePasswordAuthenticationToken 를 만든다.
            Authentication authentication = tokenProvider.getAuthentication(jwt);

            // 1-5) 세션 영역에 있는 SecurityContext 에 Authentication 객체를 저장하는 녀석
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다., uri: {}", authentication.getName(), requestURI);
        } else{
            log.debug("유효한 JWT 토큰이 없습니다. uri:{}", requestURI);
        }

        // 1-6) 다음 필터가 있으면 필터를 호출하고 없으면 컨트롤러와 매핑시켜줄 Dispatcher Servlet가 매핑 시켜줌.
        filterChain.doFilter(servletRequest, servletResponse);
    }

    // 2) Request Header에서 Bearer Token을 꺼내온다.
    //    접두사인 Bearer를 잘라내고, 진짜 token 부분만 다시 반환한다.
    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        // 2-1)
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return  bearerToken.substring(7);
        }

        return null;
    }
}
/*
* Filter vs Interceptor vs AOP
* -> Filter: Spring 진입 전, 진입 후 전 처리, 후처리 담당 (Spring과 완전 무관한 내용도 처리 가능! )
* -> Interceptor: 디스패처 서블릿의 컨트롤러 매핑 후 실행되는 녀석, Controller 가 동작하기 전에 사전 처리 해줌
* -> AOP: Proxy 객체 (대리 객체)를 만들고, 사용자가 지목한 매소드 앞 뒤로 전처리, 후처리기를 넣어서 실행
*
* 1) GenericFilterBean
* : Filter 는 Spring 에 들어가기 전에 실행되는 녀석들이다.
*   따라서 스프링 컨테이너에 존재하는 Bean을 들고와서 사용할 수가 없다.
*   GenericFilterBean은 스프링 컨테이너에 있는 빈들을 끌고와서 Filter에서 사용할 수 있도록 해주는 녀석이다.
*
* 1-3)
* 요청 전체 경로: http://127.0.0.1:8080/contextpath/servlcetpath/index.jsp?seq=1&type=NOTICE
* RequestURI()를 썼을 때 가져올 수 있는 부분: /contextpath/servlcetpath/index.jsp
*
* 1-5)
*   SecurityContext란? SecurityContext에 인증 객체를 저장하게 되면 해당 인증 객체를 프로젝트 전역에서 사용할 수 있다.
*   그럼 다수의 사용자가 서버에 접속 했을 시, SercurityContext는 이를 어떻게 해결하는가
*   인증 객체는 SecurityContextHolder라는 전역 객체 안의 SecurityContext라는 인증 객체 저장소에 저장되는데,
*   이때 SecurityContextHolder 하나 당 각기 다른 ThreadLocal에 저장된다.
*   따라서 사용자가 많을 경우, 해당 SecurityContextHolader를 담은 스레드 수가 많아져서 각자 병렬적으로 일을 처리한다.
*
* 2-1) StringUtils.hasText(value)
*   :  value가 공백이거나 null이면 false, 값이 하나라도 있으면 true 출력
*
* */
