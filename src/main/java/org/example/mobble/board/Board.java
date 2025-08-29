package org.example.mobble.board;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

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

    // 카테고리
    private Integer categoryId;
    
    // 글 작성 시간
    @CreationTimestamp
    private Timestamp createdAt;
    
    // 글 수정 시간
    @UpdateTimestamp
    private Timestamp updatedAt;

    @Builder
    public Board(Integer id, String title, String content, Integer userId, Integer views, Integer categoryId, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.views = views;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
