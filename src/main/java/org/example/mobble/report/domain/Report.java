package org.example.mobble.report.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mobble.board.domain.Board;
import org.example.mobble.user.domain.User;

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
    private ReportCase result;

    // 기타 신고 사유
    private String resultEtc;

    // 신고 내용
    private String content;

    @Builder
    public Report(Integer id, Board board, User user, ReportCase result, String resultEtc, String content) {
        this.id = id;
        this.board = board;
        this.user = user;
        this.result = result;
        this.resultEtc = resultEtc;
        this.content = content;
    }

    // 신고 서비스에서 처리
//    public Report(User user, Integer boardId, BoardRequest.BoardReportDTO reqDTO) {
//        this.boardId = boardId;
//        this.userId = user.getId();
//        try {
//            this.result = ReportCase.valueOf(reqDTO.getResult());
//        } catch (IllegalArgumentException e) {
//            this.result = ReportCase.ETC;
//            this.resultEtc = reqDTO.getResult();
//        }
//        this.content = reqDTO.getContent();
//
//    }
    public void updateResultEtc(String resultEtc) {
        this.resultEtc = resultEtc;
    }
}

