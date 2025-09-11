package org.example.mobble.report.dto;

import lombok.Data;
import org.example.mobble.board.domain.Board;
import org.example.mobble.report.domain.Report;
import org.example.mobble.report.domain.ReportCase;
import org.example.mobble.report.domain.ReportStatus;
import org.example.mobble.user.domain.User;

import java.sql.Timestamp;

public class ReportResponse {

    //list 용
    @Data
    public static class ReportDTO {
        private Integer id; //신고 id
        private ReportCase result; //신고 유형
        private String resultEtc; //신고 유형
        private ReportStatus status; //신고 처리 상태
        private String content; // 신고자
        private String boardTitle; //게시물 제목

        public ReportDTO(Report report) {
            this.id = report.getId();
            this.result = report.getResult();
            this.status = report.getStatus();
            this.content = report.getContent();
            this.boardTitle =report.getBoard().getTitle();
            this.resultEtc = report.getResultEtc();
        }


    }

    //업데이트 확인용
    @Data
    public static class ReportUpateDTO{

        private Integer id;
        private ReportCase result;
        private String resultEtc;
        private String content;

        public ReportUpateDTO(Report report) {
            this.id = report.getId();
            this.result = report.getResult();
            this.resultEtc = report.getResultEtc();
            this.content = report.getContent();
        }
    }
    @Data
    public static class ReportDetailDTO {
        private Integer id;
        private ReportCase result;
        private String resultEtc;
        private String content;
        private String boardTitle;

        public ReportDetailDTO(Report report) {
            this.id = report.getId();
            this.result = report.getResult();
            this.resultEtc = report.getResultEtc();
            this.content = report.getContent();
            this.boardTitle = report.getBoard().getTitle();
        }
    }
}
