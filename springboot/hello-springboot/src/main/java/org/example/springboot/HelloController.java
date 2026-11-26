package org.example.springboot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "world") String name) {
        return "Hello, " + name + "!";
    }

    @GetMapping("/probe/text")
    public String probeText() {
        return "ok";
    }

    @GetMapping("/probe/json")
    public Map<String, Object> probeJson() {
        return Map.of("msg", "ok", "code", 200);
    }
}
