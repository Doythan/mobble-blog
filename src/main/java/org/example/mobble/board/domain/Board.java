package org.example.mobble.board.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mobble.board.dto.BoardRequest;
import org.example.mobble.bookmark.domain.Bookmark;
import org.example.mobble.category.Category;
import org.example.mobble.report.domain.Report;
import org.example.mobble.user.domain.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

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
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // 카테고리
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    // 조회수
    private Integer views;

    // 글 작성 시간
    @CreationTimestamp
    private Timestamp createdAt;

    // 글 수정 시간
    @UpdateTimestamp
    private Timestamp updatedAt;

    //연관 신고는 게시글이 삭제되면 자동 삭제 처리
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "board")
    private List<Report> reports;

    //연관 북마크는 게시글 삭제시 자동 삭제 처리
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "board")
    private List<Bookmark> bookmarks;

    @Builder
    public Board(Integer id, String title, String content, User user, Integer views, Category category, Timestamp createdAt, List<Report> reports, Timestamp updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
        this.views = views;
        this.category = category;
        this.createdAt = createdAt;
        this.reports = reports;
        this.updatedAt = updatedAt;
    }

    public void update(BoardRequest.BoardUpdateDTO reqDTO) {
        this.title = reqDTO.getTitle();
        this.content = reqDTO.getContent();
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
