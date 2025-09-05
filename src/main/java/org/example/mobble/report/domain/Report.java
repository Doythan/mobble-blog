package org.example.mobble.report.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mobble.board.domain.Board;
import org.example.mobble.report.dto.ReportRequest;
import org.example.mobble.user.domain.User;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "report_tb")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // 테이블 설정 후 추가

    // 신고할 게시글
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    // 신고하는 유저
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // 신고 사유
    @Enumerated(EnumType.STRING)
    private ReportCase result;

    // 기타 신고 사유
    private String resultEtc;

    // 신고 내용
    private String content;

    // 신고 상태
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    //작성 시간
    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public Report(Integer id, Board board, User user, ReportCase result, String resultEtc, String content) {
        this.id = id;
        this.board = board;
        this.user = user;
        this.result = result;
        this.resultEtc = resultEtc;
        this.content = content;
    }

    public void updateResultEtc(String resultEtc) {
        this.resultEtc = resultEtc;
    }

    public void updateStauts(ReportStatus status) {
        this.status = status;
    }

    public void updateInfo(ReportRequest.ReportUpateDTO reqDTO) {

        this.result =reqDTO.getResult();
        this.resultEtc = reqDTO.getResultEtc();
        this.content = reqDTO.getContent();
    }
}

