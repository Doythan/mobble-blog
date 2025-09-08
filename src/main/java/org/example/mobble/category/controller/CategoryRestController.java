package org.example.mobble.category.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.mobble._util.error.ex.Exception401;
import org.example.mobble.category.dto.CategoryRequest;
import org.example.mobble.category.dto.CategoryResponse;
import org.example.mobble.category.service.CategoryService;
import org.example.mobble.user.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryRestController {

    private final CategoryService categoryService;
    private final HttpSession session;

    // 내 카테고리 목록
    @GetMapping
    public List<CategoryResponse.CategoryItemDTO> myCategories() {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) throw new Exception401("로그인이 필요합니다.");
        return categoryService.getMyCategoryItems(sessionUser.getId());
    }

    // 카테고리 생성
    @PostMapping
    public CategoryResponse.CategoryItemDTO categoryCreate(@RequestBody CategoryRequest.CategorySaveDTO req) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) throw new Exception401("로그인이 필요합니다.");

        var saved = categoryService.addCategory(sessionUser.getId(), req.getCategory());
        return CategoryResponse.CategoryItemDTO.builder()
                .id(saved.getId())
                .category(saved.getCategory())
                .build();
    }

    // 카테고리 이름 변경
    @PatchMapping("/{id}")
    public CategoryResponse.CategoryItemDTO categoryRename(@PathVariable Integer id,
                                                   @RequestBody CategoryRequest.CategoryUpdateDTO req) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) throw new Exception401("로그인이 필요합니다.");

        var updated = categoryService.renameCategory(sessionUser.getId(), id, req.getCategory());
        return CategoryResponse.CategoryItemDTO.builder()
                .id(updated.getId())
                .category(updated.getCategory())
                .build();
    }

    // 카테고리 삭제
    @DeleteMapping("/{id}")
    public Map<String, Object> categoryDelete(@PathVariable Integer id) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) throw new Exception401("로그인이 필요합니다.");

        categoryService.deleteCategory(sessionUser.getId(), id);
        return Map.of("success", true, "id", id); // 응답 바디에 성공 여부만 내려줌
    }
}
