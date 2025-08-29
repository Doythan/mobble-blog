package org.example.mobble.bookmark;

import jakarta.persistence.*;
import lombok.*;
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

    private Integer boardId;
    private Integer userId;

    // 만든 시간
    @CreationTimestamp
    private Timestamp createdAt;

    // 북마크 한 것들도 카테고리별 정렬이 좋을까?
//    private String category;


    @Builder
    public Bookmark(Integer id, Integer boardId, Integer userId, Timestamp createdAt) {
        this.id = id;
        this.boardId = boardId;
        this.userId = userId;
        this.createdAt = createdAt;
    }
}
