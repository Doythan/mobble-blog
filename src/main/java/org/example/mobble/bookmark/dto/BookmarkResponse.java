package org.example.mobble.bookmark.dto;

import lombok.Data;
import org.example.mobble.board.domain.Board;
import org.example.mobble.board.dto.BoardResponse;
import org.example.mobble.bookmark.domain.Bookmark;
import org.example.mobble.user.domain.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class BookmarkResponse {

    @Data
    public static class BookmarkSaveDTO {
        private Integer boardId;
        private Integer userId;
        private Timestamp createAt;

        public BookmarkSaveDTO(Bookmark bookmark) {
            this.boardId = bookmark.getBoard().getId();
            this.userId = bookmark.getUser().getId();
            this.createAt = bookmark.getCreatedAt();
        }
    }

    @Data
    public static class BookmarkDTO {
        private Integer bookId;
        private BoardResponse.DTO board;
        private Boolean isBookmark;
        private Timestamp createAt;

        public BookmarkDTO(Bookmark  bookmark) {
            Board temp = bookmark.getBoard();
            this.bookId = bookmark.getId();
            this.board =
                    BoardResponse.DTO.builder()
                            .board(temp)
                            .bookmarkCount(temp.getBookmarks().size())
                            .image(null)
                            .category(temp.getCategory())
                            .user(temp.getUser())
                            .build();
            this.isBookmark = true;
            this.createAt = bookmark.getCreatedAt();
        }
    }

    @Data
    public static class BookmarkListDTO {
        private boolean isList;
        private List<BookmarkDTO> bookmarksList;

        public BookmarkListDTO(List<Bookmark> bookmarks) {
            this.isList = bookmarks != null && !bookmarks.isEmpty();
            this.bookmarksList = bookmarks.stream().map(BookmarkDTO::new).collect(Collectors.toList());
        }
    }
}
