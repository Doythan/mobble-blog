package org.example.mobble.report;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    
    // 신고 제목
    // 신고용 카테고리를 정해두는 것이 더욱 좋을 것 같은데, 케이스도 설정해야 할 것 같음
    private String title;
    
    // 신고 내용
    private String content;
}

