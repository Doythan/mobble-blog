package org.example.mobble.bookmark.domain;

public enum BookmarkSortType {
    CREATE_AT_DESC("createAt_DESC"),
    VIEWS_DESC("views_DESC"),
    BOOK_COUNT_DESC("bookCount_DESC");

    private final String value;

    BookmarkSortType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // 문자열을 enum으로 변환하는 헬퍼
    public static BookmarkSortType from(String value) {
        for (BookmarkSortType type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return CREATE_AT_DESC; // 기본값
    }
}

