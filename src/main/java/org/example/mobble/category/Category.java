package org.example.mobble.category;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "category_tb")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 만든 사람
    private Integer userId;

    // 카테고리명
    private String category;

    @Builder
    public Category(Integer id, Integer userId, String category) {
        this.id = id;
        this.userId = userId;
        this.category = category;
    }
}
