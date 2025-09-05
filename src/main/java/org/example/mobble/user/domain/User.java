package org.example.mobble.user.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mobble.board.domain.Board;
import org.example.mobble.bookmark.domain.Bookmark;
import org.example.mobble.category.Category;
import org.example.mobble.report.domain.Report;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user_tb")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // 테이블 설정 후 추가

    // 아이디
    private String username;

    // 비밀번호
    private String password;

    // 이메일
    // 이메일 인증도 추가할 건지?
    private String email;

    private String profileImage;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Board> boards;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Bookmark> bookmarks;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Report> reports;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Category> categories;

    // reCAPTCHA 추가? : 봇 생성 방지용

    @Builder
    public User(Integer id, String username, String password, String email, String profileImage, List<Board> boards, List<Bookmark> bookmarks, List<Report> reports, List<Category> categories) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.profileImage = profileImage;
        this.boards = boards;
        this.bookmarks = bookmarks;
        this.reports = reports;
        this.categories = categories;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}

