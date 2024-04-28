package spring_with_jwt2.dto;

import lombok.Getter;
import lombok.Setter;


// 로그인 창에 명세한 아이디, 패스워드
@Getter
@Setter
public class JoinDTO {

    private String username;

    private String password;
}
