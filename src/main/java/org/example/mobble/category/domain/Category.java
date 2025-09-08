package org.example.mobble.category.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.mobble.board.domain.Board;
import org.example.mobble.user.domain.User;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "category_tb",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "category"})
)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 만든 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")  // FK 명시
    private User user;

    // 카테고리명
    private String category;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Board> boards;

    @Builder
    public Category(Integer id, User user, String category, List<Board> boards) {
        this.id = id;
        this.user = user;
        this.category = category;
        this.boards = boards;
    }

    public void updateCategory(String category) {
        this.category = category;
    }

}
