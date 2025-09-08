package org.example.mobble.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.mobble.category.domain.Category;
import org.example.mobble.category.dto.CategoryRequest;
import org.example.mobble.category.dto.CategoryResponse;
import org.example.mobble.category.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Transactional
public class CategoryScenarioPrintTest {

    @Autowired
    CategoryService categoryService;

    // data.sql에 user_id=1이 있다고 가정
    private final Integer userId = 1;

    private final ObjectMapper om =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    void scenario_with_request_and_response() throws Exception {
        // 0) 초기 목록 조회
        List<Category> base = categoryService.getCategoriesByUser(userId);
        logApi("GET /mypage/categories",
                /* request */ null,
                /* response */ toItemDTOs(base));

        // 1) 카테고리 추가
        CategoryRequest.CategorySaveDTO saveReq = CategoryRequest.CategorySaveDTO.builder()
                .category("스터디")
                .build();
        Category added = categoryService.addCategory(userId, saveReq.getCategory());
        logApi("POST /mypage/categories",
                saveReq,
                toItemDTO(added));

        // 2) 카테고리 이름 변경
        CategoryRequest.CategoryUpdateDTO updateReq = CategoryRequest.CategoryUpdateDTO.builder()
                .id(added.getId())
                .category("스터디-변경")
                .build();
        Category renamed = categoryService.renameCategory(userId, added.getId(), updateReq.getCategory());
        logApi("POST /mypage/categories/{id}/rename",
                updateReq,
                toItemDTO(renamed));

        // 3) 카테고리 삭제
        Integer deleteId = renamed.getId();
        // request는 단순히 PathVariable id → DTO 대신 null로 표현
        categoryService.deleteCategory(userId, deleteId);
        List<Category> afterDelete = categoryService.getCategoriesByUser(userId);
        logApi("POST /mypage/categories/{id}/delete",
                /* request */ "{id=" + deleteId + "}",   // 단순 문자열로 표현
                /* response */ toItemDTOs(afterDelete));
    }

    // ===== 헬퍼 =====

    private CategoryResponse.CategoryItemDTO toItemDTO(Category c) {
        return CategoryResponse.CategoryItemDTO.builder()
                .id(c.getId())
                .category(c.getCategory())
                .build();
    }

    private List<CategoryResponse.CategoryItemDTO> toItemDTOs(List<Category> categories) {
        return categories.stream().map(this::toItemDTO).collect(Collectors.toList());
    }

    private void logApi(String title, Object request, Object response) throws Exception {
        System.out.println("==== " + title + " ====");
        System.out.println("Request:");
        System.out.println(request == null ? "null" : om.writeValueAsString(request));
        System.out.println("Response:");
        System.out.println(response == null ? "null" : om.writeValueAsString(response));
        System.out.println();
    }
}
