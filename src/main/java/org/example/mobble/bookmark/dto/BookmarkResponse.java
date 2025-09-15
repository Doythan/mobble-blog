package org.example.mobble.bookmark.dto;

import lombok.Data;
import org.example.mobble._util.util.HtmlUtil;
import org.example.mobble.board.domain.Board;
import org.example.mobble.board.dto.BoardResponse;
import org.example.mobble.bookmark.domain.Bookmark;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
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
    public static class BookmarkListDTO {
        private boolean isList;
        private List<BookmarkDTO> bookmarksList;
        private Integer prev;
        private Integer next;
        private Integer current;
        private Integer totalCount;
        private Integer totalPage;
        private Integer size;
        private Boolean isFirst;
        private Boolean isLast;

        public BookmarkListDTO(List<Bookmark> bookmarks, int current, int bookmarkSize, long totalCount) {
            this.isList = bookmarks != null && !bookmarks.isEmpty();
            this.bookmarksList = bookmarks.stream().map(BookmarkDTO::new).collect(Collectors.toList());
            // 페이지 정보
            this.current = current;
            this.size = bookmarkSize;
            this.totalCount = (int) totalCount;
            this.totalPage = makeTotalPage((int) totalCount, bookmarkSize);

            this.prev = current - 1;
            this.next = current + 1;
            this.isFirst = current == 0;
            this.isLast = current >= totalPage - 1;
        }
    }

    @Data
    public static class BookmarkDTO {

        private Integer bookId;
        private BoardResponse.DTO board;
        private Boolean isBookmark;
        private String createAt;
        private String image;
        private String excerpt;

        public BookmarkDTO(Bookmark bookmark) {
            Board temp = bookmark.getBoard();
            this.bookId = bookmark.getId();
            this.board = BoardResponse.DTO.builder()
                    .board(temp)
                    .bookmarkCount(temp.getBookmarks().size())
                    .image(temp.getThumbnailUrl())
                    .category(temp.getCategory())
                    .user(temp.getUser())
                    .build();
            this.isBookmark = true;
            this.image = temp.getThumbnailUrl() == null ? null : temp.getThumbnailUrl();
            this.excerpt = HtmlUtil.extractFirstParagraph(temp.getContent(), 100);

            // Timestamp → yyyy-MM-dd
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.createAt = bookmark.getCreatedAt().toLocalDateTime().format(formatter);

        }
    }

    private static Integer makeTotalPage(int totalCount, int size) {
        return totalCount / size + (totalCount % size == 0 ? 0 : 1);
    }
}
