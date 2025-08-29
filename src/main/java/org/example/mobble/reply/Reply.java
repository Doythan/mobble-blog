package org.example.mobble.reply;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

// LiveRe 패키지 사용 시 : 댓글 관리가 필요 없음 -> 라이브리 로그인 후 관리 가능
// 중간 광고 처리를 HTML/CSS 선에서 정리하거나 상관없다고 하고 진행할거면 댓글 테이블은 없어져도 될 듯함

@Data
@NoArgsConstructor
@Entity
@Table(name = "reply_tb")
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // 테이블 설정 후 추가
}

