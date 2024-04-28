package spring_with_jwt2.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;


    // 로그인 시도 함수 -> 요청을 가로채서 그 안에 들어있는, username 과 password를 검증한다.
    @Override
    public Authentication attemptAuthentication (HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {


        // 1. 요청으로부터 username과 password를 가져오기
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        System.out.println(username);

        // 2. 실제 인증을 진행하는 Authentication Manager 한테 username 과 password를 줘야한다.
        //    이때 그냥 주는 것이 아니고, DTO 처럼 특정 규격에 맞게 보내야 한다. 이 규격에 해당하는 것이 authToken 이다. 인수: (username, password, 허용되는 권한 목록)
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);


        // 3. [AuthenticationToken]에 대해 자동으로 검증을 해주는 AutenticationManager를 주입 받아서, 해당 객체에 token을 전달한다.
        // 검증방법: userDetails가 DB에서 회원 정보들을 긁어오는데, 거기에 현재 주입받은 token의 username과 password가 존재하는지 확인하는 식으로 이루어진다.
        return authenticationManager.authenticate(authToken);

    }

    // 4. 로그인 성공 시 실행되는 매소드 => 여기서 JWT 토큰을 발급하면 된다.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication){

    }

    // 5. 로그인 실패 시 실행되는 매소드 => 관련 에러 처리를 맡아서 하면 된다.
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed){

    }
}
