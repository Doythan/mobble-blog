package org.example.mobble.board.domain;

import lombok.Getter;
import org.springframework.data.domain.Sort;

@Getter
public enum SearchOrderCase {
    VIEW_COUNT_ASC("views", Sort.Direction.ASC),
    CREATED_AT_ASC("createdAt", Sort.Direction.ASC),
    BOOKMARK_COUNT_ASC("bookmarkCount", Sort.Direction.ASC),

    CREATED_AT_DESC("createdAt"),
    VIEW_COUNT_DESC("views"),
    BOOKMARK_COUNT_DESC("bookmarkCount");

    private final String column;
    private final Sort.Direction direction;

    SearchOrderCase(String column, Sort.Direction direction) {
        this.column = column;
        this.direction = direction;
    }

    SearchOrderCase(String column) {
        this.column = column;
        this.direction = Sort.Direction.DESC;
    }

    public Sort.Order toOrder() {
        return new Sort.Order(direction, column);
    }

    // 엔티티 필드로 정렬 가능한 케이스만 true
    public boolean isJpaFieldSortable() {
        return this != BOOKMARK_COUNT_ASC && this != BOOKMARK_COUNT_DESC;
    }
}
