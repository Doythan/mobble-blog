package org.example.mobble;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HealthController {

    @GetMapping("/")
    public String health() {
        return "main";
    }
}
