package org.example.mobble.board.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mobble.report.domain.ReportCase;

public class BoardRequest {
    @Data
    @NoArgsConstructor
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
    @NoArgsConstructor
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
    @NoArgsConstructor
    public static class ReportSaveDTO {
        ReportCase result;
        String content;
        String resultEtc;

    }
}
