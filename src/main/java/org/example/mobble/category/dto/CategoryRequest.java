package org.example.mobble.category.dto;

import lombok.Builder;
import lombok.Data;

public class CategoryRequest {

    // 카테고리 생성
    @Data
    public static class CategorySaveDTO {
        String category;

        @Builder
        public CategorySaveDTO(String category) {
            this.category = category;
        }
    }

    // 카테고리 이름 변경
    @Data
    public static class CategoryUpdateDTO {
        Integer id;       // 대상 카테고리 PK
        String category;  // 새 이름

        @Builder
        public CategoryUpdateDTO(Integer id, String category) {
            this.id = id;
            this.category = category;
        }
    }
}
