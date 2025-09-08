package org.example.mobble.bookmark.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mobble.board.domain.Board;
import org.example.mobble.user.domain.User;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "bookmark_tb")
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // 테이블 설정 후 추가

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // 만든 시간
    @CreationTimestamp
    private Timestamp createdAt;

    // 북마크 한 것들도 카테고리별 정렬이 좋을까?
//    private String category;


    @Builder
    public Bookmark(Integer id, Board board, User user, Timestamp createdAt) {
        this.id = id;
        this.board = board;
        this.user = user;
        this.createdAt = createdAt;

        if (board != null) {
            board.getBookmarks().add(this); // 연관관계 편의 메서드
        }
    }
}
