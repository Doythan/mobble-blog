package org.example.mobble.admin.dto;

import lombok.Data;
import org.example.mobble.board.domain.Board;
import org.example.mobble.report.domain.Report;
import org.example.mobble.report.domain.ReportCase;
import org.example.mobble.report.domain.ReportStatus;
import org.example.mobble.user.domain.User;

import java.sql.Timestamp;

public class AdminResponse {

    //리스트용
    @Data
    public static class ReportDTO {
        private Integer id; //신고 id
        private ReportCase result; //신고 유형
        private ReportStatus status; //신고 처리 상태
        private String reportingUsername; // 신고자
        private String reportedUsername; // 게시글 작성자
        private String boardTitle; //게시물 제목
        private Timestamp createdAt; //생성일자


        public ReportDTO(Board board, Report report) {
            this.id = report.getId();
            this.result = report.getResult();
            this.status = report.getStatus();
            this.reportingUsername = report.getUser().getUsername();
            this.reportedUsername = board.getUser().getUsername();
            this.boardTitle = board.getTitle();
             this.createdAt = report.getCreatedAt();
        }
    }

    @Data
    public static class ReportDetailDTO {

        Integer  id;
        String content;
        ReportStatus status;
        ReportCase result;
        Timestamp createdAt;
        String reportedUsername;
        String reportingUsername;
        String  resultEtc;
        String boardTitle;


        public ReportDetailDTO(Report report) {

            this.id = report.getId();
            this.content = report.getContent();
            this.status = report.getStatus();
            this.result = report.getResult();
            this.createdAt = report.getCreatedAt();
            this.reportedUsername = report.getBoard().getUser().getUsername();
            this.reportingUsername = report.getUser().getUsername();
            this.resultEtc = report.getResultEtc();
            this.boardTitle = report.getBoard().getTitle();
        }
    }
}
