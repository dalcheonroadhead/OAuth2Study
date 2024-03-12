package com.ssafy.oauth.oauthtest.controller;

import com.ssafy.oauth.oauthtest.service.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


/*
* Redirect 되어서 들어오는 곳을 설정
* ex) ~~/login/oauth2/code/{인가코드를 보내는 곳}
* -> 각 API 설정에다가 내가 직접 적어놓은 주소임
* */
@RestController
@AllArgsConstructor
@RequestMapping(value = "/login/oauth2", produces = "application/json")
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/code/{registrationId}")
    public void googleLogin(@RequestParam String code, @PathVariable String registrationId){
        loginService.socialLogin(code, registrationId);

    }

}
