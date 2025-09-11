package org.example.mobble.category.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.mobble._util.error.ex.Exception401;
import org.example.mobble.category.dto.CategoryRequest;
import org.example.mobble.category.service.CategoryService;
import org.example.mobble.user.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/mypage/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final HttpSession session;

    // 마이페이지(내 피드) > 카테고리 관리 화면
    @GetMapping
    public String categoriesPage(HttpServletRequest request) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) throw new Exception401("로그인이 필요합니다.");
        request.setAttribute("model", categoryService.getCategoriesWithCount(sessionUser.getId()));
        return "mypage/category/category-page";
    }

    // 카테고리 추가
    @PostMapping
    public String addCategory(CategoryRequest.CategorySaveDTO req) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) throw new Exception401("로그인이 필요합니다.");
        categoryService.addCategory(sessionUser.getId(), req.getCategory()); // 빈값 금지(마이페이지 정책)
        return "redirect:/mypage/categories";
    }

    // 카테고리 이름 변경
    @PostMapping("/{categoryId}/rename")
    public String renameCategory(@PathVariable Integer categoryId, CategoryRequest.CategoryUpdateDTO req) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) throw new Exception401("로그인이 필요합니다.");
        categoryService.renameCategory(sessionUser.getId(), categoryId, req.getCategory());
        return "redirect:/mypage/categories";
    }

    // 카테고리 삭제 (폼 전송)
    @PostMapping("/{categoryId}/delete")
    public String deleteCategory(@PathVariable Integer categoryId) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) throw new Exception401("로그인이 필요합니다.");
        categoryService.deleteCategory(sessionUser.getId(), categoryId);
        return "redirect:/mypage/categories";
    }
}
