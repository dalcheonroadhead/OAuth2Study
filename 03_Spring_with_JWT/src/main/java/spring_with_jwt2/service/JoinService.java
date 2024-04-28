package spring_with_jwt2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import spring_with_jwt2.dto.JoinDTO;
import spring_with_jwt2.entity.UserEntity;
import spring_with_jwt2.repository.UserRepository;

@Service
@RequiredArgsConstructor // 빈 객체를 주입 받으려면, 연결 고리 역할을 하는 생성자가 필요하다. (권장되는 생성자 주입)
// 이를 Lombok 의 어노테이션으로 해결한다.
public class JoinService {

    // 1. 스프링 빈 명부에 명세된, Bean 중 타입이 일치하는 Bean 을 주입 받는다.
    private final UserRepository userRepository;

    // 2. 암호를 그냥 DB에 저장하면, DB가 해커에게 털렸을 경우, 그대로 DB 정보를 사용할 수 있다.
    //    치명적이다. 따라서 [password]는 Spring Security 에서 작성했던, 패스워드 암호화 함수로 암호화 한다.
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 2. 회원가입을 진행하는 매소드 입니다.
    public void  joinProcess(JoinDTO joinDTO) {

        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        // 2-1) 해당 유저이름으로 회원이 존재하는지 확인 합니다.
        Boolean isExist = userRepository.existsByUsername(username);

        // 2-2) 만약 회원가입이 온 UserName으로 이미 회원이 존재한다면, 로그인 과정을 강제 종료합니다.
        // 실습이라서 쉽게 표현했지만, 이미 존재하는 경우 바로 AccessToken을 주거나,
        // 클라이언트에게 이미 존재하는 회원이라고 알려줄 수도 있습니다.
        if(isExist) {
            return;
        }

        // 2-3) 신규 유저라면 회원 정보 저장 작업을 진행합니다.
        UserEntity data = new UserEntity();

        data.setUsername(username);

        // [Password]를 암호화 하여 집어넣는다.
        data.setPassword(bCryptPasswordEncoder.encode(password));

        // Spring의 경우 권한 설정 시 권한 앞에 ROLE 이란 접두사를 붙인 후에 뒤에 원하는 단어를 적어야 한다.
        data.setRole("ROLE_ADMIN");

        userRepository.save(data);
    }
}
