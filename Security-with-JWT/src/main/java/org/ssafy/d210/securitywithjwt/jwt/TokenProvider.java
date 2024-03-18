package org.ssafy.d210.securitywithjwt.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;


/*
* JWT 라이브러리를 이용하여 JWT를 생성하고 검증하는 컴포넌트
* */
@Slf4j
@Component
public class TokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";


    // 0.Key를 만드는 재료
    private final String secret;

    // 1.토큰 만료 시간
    private final long tokenValidityMilliseconds;

    private Key key;

    // 1) 생성자 -> 서버 키와 토큰 만료 시간을 받아온다.
    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInseconds
    ) {
        this.secret = secret;
        this.tokenValidityMilliseconds = tokenValidityInseconds;
    }


    // 일련의 랜덤 수를 이용하여 만든 Secret-Key를 가지고 JWT 전자서명(C부분)에 사용할 전자키를 만듦.
    @Override
    public void afterPropertiesSet() throws Exception {
        // Secret key를 디코딩하여 KeyBytes 형태로 변환
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        // 변환된 값을 HMAC 알고리즘을 이용하여 해쉬화 -> 해쉬화된 값을 서명용 키로 사용
        // jjwt.0.9.x 버전까지는 HMAC 알고리즘 시리즈 중에 무엇을 써야할지 지정해줘야 했지만,
        // 그 이후로는 적절한 HMAC 알고리즘을 알아서 지정해준다.
        // 여기서는 HS256 알고리즘을 써서 해쉬화 하였다.
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 2) 인증 정보를 받아서 토큰 생성하기 -> 토큰은 Access Token으로 쓰이거나 Refresh Token으로 쓰인다.
    public String createToken(Authentication authentication) {
        log.info("{}",authentication);

        // 2-1) 권한 모음집
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                // 3), 4)
                .collect(Collectors.joining(","));

        // 2-2) 토큰의 만료기한을 설정 (현 시간 + 만료 수명)
        long now = (new Date()).getTime();

        // 만료 기한
        Date validity = new Date(now + this.tokenValidityMilliseconds);


        return Jwts.builder()
                .setSubject(authentication.getName())   // 토큰의 용도를 명시
                .claim(AUTHORITIES_KEY, authorities)    // 토큰의 body인 payLoad에 담길 Claim값을 설정
                .signWith(key, SignatureAlgorithm.HS512)// 어떤 알고리즘과 키값을 이용해 JWT의 전자 서명 부분인 C를 만들 것인지 설정
                                                        // 해당 알고리즘으로 header + payload 인코딩 하고, 마지막엔 severkey와 함께 해싱도 함
                .setExpiration(validity)                // 만료 기한 설정
                .compact();                             // build()랑 같은 말. 마지막으로 토큰을 생성함.

    }

    // 5) 토큰을 parserbuilder를 통해 해부함. 거기서 인증 정보를 꺼내서 사용하기 위한 매소드
    public Authentication getAuthentication(String token) {


        // 5-1) 들어온 토큰을 역직렬화 하여 해부해서 쓸 수 있는 상태로 만든다.
        Claims claims = Jwts
                .parserBuilder()        // jwtParser를 만들기 위한 builder jwtParser란? 직렬화된 jwt를 다시 jwt 객체로 바꾸는 녀석
                                        // 바뀐 객체는 다시 Header와 Payload, 전자서명으로 나뉘어져서 우리가 쓸 수 있게 됨
                .setSigningKey(key)     // JWS 형태의 JWT의 전자서명 부분을 검증하는데 사용할 key가 무엇인지 세팅
                .build()                // builder() 실행해서 jwtParser를 실행
                .parseClaimsJws(token)  // 위에 세팅한 key 와 인수로 들어온 토큰에서 인코딩된 header 와 body 를 다시 해쉬화 시켜서 나온 값이
                                        // 본 JWT의 전자서명부분과 일치하는지 체크 -> 값이 다르면 변조된거임.
                                        // 근데 이거 전에 유효성 검증 통과한거만 들어와서 유효성 검사 불통과는 없을 거다.
                .getBody();

        // 5-2) 해당 토큰이 가지고 있는 권한 목록을 리스트 형태로 받는다.
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(",")) // ,로 권한을 구분한다.
                        .map(SimpleGrantedAuthority::new) // 구분된 권한마다 새로 SimpleGrantedAuthority 객체로 만들어서
                        .collect(Collectors.toList());  // list에 추가한다.

        // 5-3) 사용자 인증 정보를 담은 USER 객체를 생성
        // 인수는 순서대로, userName, password, 권한 리스트 이다.
        User principal = new User(claims.getSubject(), "", authorities);


        // 5-4) 다시 유저 객체와 그 객체만의 token, 권한 리스트를 담은 인증 객체를 생성
        // 해당 객체는 Autheticaion 클래스를 구현한 하위 클래스이고,
        // 이렇게 인수가 3개 들어가는 경우, 인증이 완료된 객체를 의미한다.
        // 우리는 이 객체를 로직 내부에서 사용할 수 있다.
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // 6) 토큰의 유효성을 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명 입니다. payLoad 혹은 header가 위조되었습니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 유형의 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다. ");
        }
        return  false;
    }
}
/*
* 1) InitializingBean
*   : 빈 생명 주기에 접근하여, 빈 생성 및 의존관계 주입 이후의, '초기화 작업'을 따로 빼내어서
*     사용자가 커스터마이징 할 수 있도록 해주는 것
*     스프링 빈의 이벤트 라이프사이클
      스프링 컨테이너 생성 -> 스프링 빈 생성 -> 의존관계 주입 -> 초기화 콜백 -> 사용 -> 소멸전 콜백 -> 스프링 종료

* 2) Authentication
*   : 인증 객체로 안에 다음과 같은 메소드들을 가지고 있다.
*       a. getPrincipal(): 사용자의 아이디 혹은 User 객체를 저장하고 있음
*       b. getCredientials(): 사용자의 비밀번호를 가지고 있음
*       c. getAuthorities(): 인증된 사용자의 권한 목록이 들어있음
*       d. getDetails(): 사용자의 인증 부가 정보를 가지고 있다.
*       e. isAuthenticated(): 인증 되었는지 안 되었는지 여부가 boolean으로 들어있다.
*
*  3) collect()
*    : Stream 아이템들을 List 혹은 Set 자료형으로 변환
*
*  4) Collectors.joining(",")
*    : Stream에서 쓰는 함수로 스트림에 들어온 리스트 원소를 모두 한줄로 이어서 StringBuilder에 append 해주는 함수
*      만약 여기서 joining(",")을 넣어두면, 스트림에 들어온 원소 끝마다 ,를 붙인뒤에 StringBuilder로 이어버린다.
*
*  5) JWS(Json Web Signature)
*    :  서버에서 인증을 근거로 인증 정보를 서버의 PK로 서명한 것을 토큰화 한 것
*       -> JWT를 구현하는 방법 2개 중 하나
*       -> JWS는 header + payload + signature로 되어있다.
*       -> 이론에서 배웠듯이, signature 은 header + payload + serverkey를 해쉬 알고리즘으로 해쉬화 한 것
*
*       5-2) SimpleGrantedAuthority 클래스는 ROLE이란 멤버변수를 가지고 있다.
*            ROLE이란 클래스와 차이는 모르겠다.
*
*  6)
* */