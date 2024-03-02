package com.oauth2.login.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConfig {

    // RestTemplate를 사용. -> SpringBoot 3.0 부터 지원하고 있으며, Restful service를 더 용이하게 해주는 녀석
    // URLconnect와 HttpClient 상위호환
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
