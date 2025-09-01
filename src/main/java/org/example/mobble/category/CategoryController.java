package org.example.mobble.category;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class CategoryController {
    private final CategoryService categoryService;
    private final HttpSession session;
}
