package org.example.mobble.bookmark;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // 북마크 한 것들도 카테고리별 정렬이 좋을까?
    private String category;
}
