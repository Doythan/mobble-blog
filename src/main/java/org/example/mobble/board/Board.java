package org.example.mobble.board;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "board_tb")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // 테이블 설정 후 추가

    private String title;
    private String content;

    // 글 쓴 사람
    private Integer userId;

    // 조회수
    private Integer views;

    // 좋아요
    private Integer likes;

    // 북마크
    private Integer bookmarks;

    // 댓글 : 라이브리 사용 시 필요 없음
    private Integer replies;

    // 카테고리
    private String category;
}
