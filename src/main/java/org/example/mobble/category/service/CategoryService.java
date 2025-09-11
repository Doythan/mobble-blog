package org.example.mobble.category.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.example.mobble._util.error.ex.Exception400;
import org.example.mobble._util.error.ex.Exception401;
import org.example.mobble._util.error.ex.Exception404;
import org.example.mobble.category.domain.Category;
import org.example.mobble.category.domain.CategoryRepository;
import org.example.mobble.category.dto.CategoryResponse;
import org.example.mobble.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 연관관계 주입을 위해 User 레퍼런스를 가져오기 위함
    @PersistenceContext
    private EntityManager em;

    // 특정 유저의 카테고리 목록 조회, (현재는 사용하는 곳 없음)
    @Transactional(readOnly = true)
    public List<Category> getCategoriesByUser(Integer userId) {
        if (userId == null) throw new Exception400("userId가 없습니다.");
        return categoryRepository.findAllByUserIdOrderByIdDesc(userId);
    }

    // 특정 유저의 카테고리 목록 + count 포함
    @Transactional(readOnly = true)
    public List<CategoryResponse.CategoryItemWithCountDTO> getCategoriesWithCount(Integer userId) {
        if (userId == null) throw new Exception400("userId가 없습니다.");
        return categoryRepository.findItemsWithCountByUserId(userId);
    }

    // 카테고리 추가
    @Transactional
    public Category addCategory(Integer userId, String categoryName) {
        if (userId == null) throw new Exception400("userId가 없습니다.");
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new Exception400("카테고리명이 비어 있습니다.");
        }
        String normalized = categoryName.trim();

        if (categoryRepository.existsByUserIdAndCategory(userId, normalized)) {
            throw new Exception400("이미 존재하는 카테고리명입니다.");
        }

        // 연관관계 주입: userId -> User 엔티티
        User user = em.find(User.class, userId);
        if (user == null) throw new Exception404("사용자를 찾을 수 없습니다.");

        Category category = Category.builder()
                .user(user)
                .category(normalized)
                .build();

        return categoryRepository.save(category);
    }

    // 카테고리 이름 변경
    @Transactional
    public Category renameCategory(Integer userId, Integer categoryId, String newName) {
        if (userId == null) throw new Exception400("userId가 없습니다.");
        if (categoryId == null) throw new Exception400("categoryId가 없습니다.");
        if (newName == null || newName.trim().isEmpty()) {
            throw new Exception400("새 카테고리명이 비어 있습니다.");
        }
        String normalized = newName.trim();

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));

        // 소유자 검증: userId -> category.user.id
        if (category.getUser() == null || !category.getUser().getId().equals(userId)) {
            throw new Exception401("본인 카테고리만 수정할 수 있습니다.");
        }

        // 동일 유저 내 중복 방지
        if (categoryRepository.existsByUserIdAndCategory(userId, normalized)) {
            throw new Exception400("이미 존재하는 카테고리명입니다.");
        }

        // 도메인 메서드가 없으므로 세터 사용 (@Data)
        category.updateCategory(normalized); // 더티체킹 반영
        return category;
    }

    // 카테고리 삭제
    @Transactional
    public void deleteCategory(Integer userId, Integer categoryId) {
        if (userId == null) throw new Exception400("userId가 없습니다.");
        if (categoryId == null) throw new Exception400("categoryId가 없습니다.");

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new Exception404("카테고리를 찾을 수 없습니다."));

        // 소유자 검증
        if (category.getUser() == null || !category.getUser().getId().equals(userId)) {
            throw new Exception401("본인 카테고리만 삭제할 수 있습니다.");
        }

        // 참조 끊기
        categoryRepository.detachBoards(categoryId);

        categoryRepository.deleteById(category.getId());
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse.CategoryItemDTO> getMyCategoryItems(Integer userId) {
        if (userId == null) throw new Exception400("userId가 없습니다.");
        return categoryRepository.findAllByUserIdOrderByIdDesc(userId)
                .stream()
                .map(c -> CategoryResponse.CategoryItemDTO.builder()
                        .id(c.getId())
                        .category(c.getCategory())
                        .build())
                .toList();
    }


    public List<String> getPopularList(Integer count) {
        return categoryRepository.getPopularList(count);
    }

    public List<String> getMyFeedPopularList(Integer count ,User user) {
        return categoryRepository.getMyFeedPopularList(count,user);
    }
}
