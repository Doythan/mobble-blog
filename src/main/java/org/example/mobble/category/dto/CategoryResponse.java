package org.example.mobble.category.dto;

import lombok.Builder;
import lombok.Data;

public class CategoryResponse {

    // 공통: 카테고리 목록/단건 표시용
    @Data
    public static class CategoryItemDTO {
        Integer id;
        String category;

        @Builder
        public CategoryItemDTO(Integer id, String category) {
            this.id = id;
            this.category = category;
        }
    }

    // 홈: 인기 카테고리 Top N
    @Data
    public static class CategoryPopularDTO {
        Integer categoryId;
        String category;
        Long postCount;

        @Builder
        public CategoryPopularDTO(Integer categoryId, String category, Long postCount) {
            this.categoryId = categoryId;
            this.category = category;
            this.postCount = postCount;
        }
    }

    // 글 상세: 해당 글의 카테고리(없을 수 있음)
    @Data
    public static class CategoryOfBoardDTO {
        Integer categoryId; // null 가능
        String category;    // null 가능

        @Builder
        public CategoryOfBoardDTO(Integer categoryId, String category) {
            this.categoryId = categoryId;
            this.category = category;
        }
    }

    // 마이페이지: 카테고리 목록 + 게시글 수
    @Data
    public static class CategoryItemWithCountDTO {
        Integer id;
        String category;
        Long boardCount;

        @Builder
        public CategoryItemWithCountDTO(Integer id, String category, Long boardCount) {
            this.id = id;
            this.category = category;
            this.boardCount = boardCount;
        }
    }
}
