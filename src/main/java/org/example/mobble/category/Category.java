package org.example.mobble.category;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mobble.board.domain.Board;
import org.example.mobble.user.domain.User;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "category_tb")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 만든 사람
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    // 카테고리명
    private String category;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private List<Board> boards;

    @Builder
    public Category(Integer id, User user, String category) {
        this.id = id;
        this.user = user;
        this.category = category;
    }
}
