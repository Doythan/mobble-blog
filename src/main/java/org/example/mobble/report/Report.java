package org.example.mobble.report;

import jakarta.persistence.*;
import lombok.*;

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
    private Integer boardId;
    
    // 신고하는 유저
    private Integer userId;
    
    // 신고 사유
    private ReportCase result;

    // 기타 신고 사유
    private String resultEtc;
    
    // 신고 내용
    private String content;

    @Builder
    public Report(Integer id, Integer boardId, Integer userId, ReportCase result, String resultEtc, String content) {
        this.id = id;
        this.boardId = boardId;
        this.userId = userId;
        this.result = result;
        this.resultEtc = resultEtc;
        this.content = content;
    }
}

