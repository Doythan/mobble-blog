package org.example.mobble;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HealthController {
    @GetMapping("/")
    public String health() {
        return "main";
    }
}
