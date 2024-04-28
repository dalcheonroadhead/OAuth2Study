package spring_with_jwt2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // =  @Controller +  @ResponseBody
public class MainController {

    @GetMapping("/")
    public String mainP() {
        return "main Controller";
    }
}
