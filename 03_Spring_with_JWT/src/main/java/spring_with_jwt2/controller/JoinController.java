package spring_with_jwt2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import spring_with_jwt2.dto.JoinDTO;
import spring_with_jwt2.repository.UserRepository;
import spring_with_jwt2.service.JoinService;

@RestController
@RequiredArgsConstructor
public class JoinController {


    private final JoinService joinService;

    // 1. 회원가입 요청이 들어오는 곳이다.
    @PostMapping("/join")
    public String joinProcess(JoinDTO joinDTO) {

        joinService.joinProcess(joinDTO);

        return "ok";
    }
}
