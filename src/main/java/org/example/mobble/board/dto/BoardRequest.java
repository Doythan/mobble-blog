package org.example.mobble.board.dto;

import lombok.Builder;
import lombok.Data;

public class BoardRequest {
    @Data
    public static class BoardSaveDTO {
        String category;
        String title;
        String content;

        @Builder
        public BoardSaveDTO(String category, String title, String content) {
            this.category = category;
            this.title = title;
            this.content = content;
        }
    }

    @Data
    public static class BoardUpdateDTO {
        Integer id;
        String category;
        String title;
        String content;

        @Builder
        public BoardUpdateDTO(Integer id, String category, String title, String content) {
            this.id = id;
            this.category = category;
            this.title = title;
            this.content = content;
        }
    }

    @Data
    public static class BoardReportDTO {
        String result;
        String content;
        String resultEtc;

        @Builder
        public BoardReportDTO(String result, String content, String resultEtc) {
            this.result = result;
            this.content = content;
            this.resultEtc = resultEtc;
        }
    }
}
