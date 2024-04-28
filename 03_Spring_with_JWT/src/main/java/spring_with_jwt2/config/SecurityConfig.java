package spring_with_jwt2.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import spring_with_jwt2.jwt.LoginFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /* 시큐리티를 거친 후, 회원 정보 저장, 시큐리티에 들어온 Guest가 우리 회원인지 인증할 때,
       쓰이는 비밀번호를 아래의 bCryptPasswordEncoder로 한번 더 암호화 한다.
       보안을 높인다.
    *
    */


    // AuthenticationManger를 만들기 위해서 필요한, 환경설정 객체는 Spring Container에 있음으로, 의존성 주입 받는다.
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }



    // LoginFilter 에서 쓰일, AuthenticationManger 객체를 빈에 등록한다.
    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // csrf disable -> 세션 방식에서는 세션이 고정되어 있어 해당 사이트 위조 공격 방지가 필요함.
        http
            .csrf((auth) -> auth.disable());

        // Form 로그인 방식 Disable -> 우리는 JWT 기반 API 서버를 만들 것임으로,
        // 인증이 안된 사용자에게 로그인 페이지를 Redirect 하는 formLogin은 필요 없으니 끈다.
        http
            .formLogin((auth) -> auth.disable());


        // http Basic 인증 방식 Disable
        // Form Login과 마찬가지로, Basic 형태 토큰이 없으면 로그인 페이지로 Redirect 시키는
        // httpBasic도 끈다.
        http
            .httpBasic((auth) -> auth.disable());

        /*
        * 로그인, 회원가입, 메인에 대한 리소스는 인증이 없이도 요청 가능
        * /admin 경로는 ADMIN 권한이 있는 사용자만 요청이 가능
        * 나머지는 인증된 사용자만 요청이 가능
        * */
        http
            .authorizeHttpRequests((auth) -> auth
                .requestMatchers("/login", "/", "/join").permitAll()
                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated());

        http
            // addFilterAt은 특정 위치의 Filter를 대신해서 Filter 등록
            // addFilterBefore는 특정 위치의 Filter 전에 Filter 등록
            // addFilterAfter는 특정 위치의 Filter 전에 Filter 등록


            // 우리가 formLogin을 Disable 하면서, UsernamePasswordAuthenticationFilter가 동작하지 않게 되었다.
            // 그것을 대신하는 LoginFilter 를 만들었고, 이제 LoginFilter 를 해당 Filter 를 대체하여 등록하면 되는 것이다.
            .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration)), UsernamePasswordAuthenticationFilter.class);


        /*
        * 세션 Stateless 설정
        * JWT 기반의 경우 세션을 서버에서 계속 유지하는 것이 아니라,
        * 한 번의 요청이 왔을 때, 임시 세션을 만들어서 사용자의 정보를 저장해놨다가,
        * 요청이 끝나면 해당 정보를 지우는 StateLess 한 방법을 쓰고 있다.
        * 따라서 세션이 기본 값인 Statefull 하지 않고 Stateless 하도록 바꾼다.
        * */

        http
            .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }
}
