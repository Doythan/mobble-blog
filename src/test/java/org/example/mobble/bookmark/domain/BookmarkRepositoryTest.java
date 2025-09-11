package org.example.mobble.bookmark.domain;

import org.example.mobble.board.domain.Board;
import org.example.mobble.board.domain.BoardRepository;
import org.example.mobble.board.dto.BoardResponse;
import org.example.mobble.bookmark.dto.BookmarkResponse;
import org.example.mobble.bookmark.service.BookmarkService;
import org.example.mobble.category.domain.Category;
import org.example.mobble.category.domain.CategoryRepository;
import org.example.mobble.user.domain.User;
import org.example.mobble.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Import({BookmarkRepository.class, BoardRepository.class, UserRepository.class, BookmarkService.class, CategoryRepository.class})
@DataJpaTest
public class BookmarkRepositoryTest {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private CategoryRepository categoryRepository;

    // 북마크 있는지 조회 (false일떄)
    @Test
    public void findByBoardIdAndUserIdFALSE_test() {
        // 1. 테스트용 User 생성 및 저장
        User testUser = User.builder()
                .username("tester")
                .build();
        userRepository.save(testUser);

        // 2. 테스트용 Board 생성 및 User 연결 후 저장
        Board testBoard = Board.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .user(testUser)
                .build();
        boardRepository.save(testBoard);

        // 3. 존재하지 않는 북마크 조회 (아직 생성 안 함)
        bookmarkRepository.findByBoardIdAndUserId(testBoard.getId(), testUser.getId())
                .ifPresentOrElse(
                        bookmark -> {
                            System.out.println(">>> 북마크 존재: BoardId = " + bookmark.getBoard().getId());
                            System.out.println(">>> 북마크 존재: UserId = " + bookmark.getUser().getId());
                        },
                        () -> System.out.println(">>> 있는지 조회 했는데 북마크 없음") // 예상: 이 출력
                );
    }

    // 북마크 있는지 조회 (true일떄)
    @Test
    public void findByBoardIdAndUserIdTRUE_test() {
        User testUser = User.builder()
                .username("tester")
                .role("user")
                .build();
        userRepository.save(testUser);

        Board testboard = Board.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .user(testUser)   // 여기서 user 지정
                .build();
        boardRepository.save(testboard);

        Bookmark bookmark = Bookmark.builder()
                .board(testboard)
                .user(testUser)   // 여기에도 같은 user 지정
                .build();
        bookmarkRepository.BookmarkSave(bookmark);

// 존재 여부 확인
        bookmarkRepository.findByBoardIdAndUserId(testboard.getId(), testUser.getId())
                .ifPresentOrElse(
                        b -> {
                            System.out.println(">>> 북마크 존재: BoardId = " + b.getBoard().getId());
                            System.out.println(">>> 북마크 존재: Board Title = " + b.getBoard().getTitle());
                            System.out.println(">>> 북마크 존재: UserId  = " + b.getUser().getId());
                        },
                        () -> System.out.println(">>> 북마크 없음")
                );
    }

    //북마크 저장
    @Test
    public void bookmarkSave_test() {
        // 1. 테스트용 User 생성 및 저장
        User testUser = User.builder()
                .username("tester")
                .build();
        userRepository.save(testUser);

        // 2. 테스트용 Board 생성 및 User 연결 후 저장
        Board testBoard = Board.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .user(testUser)
                .build();
        boardRepository.save(testBoard);

        // 3. DB에서 Board 조회
        Board boardPS = boardRepository.findById(testBoard.getId()).orElseThrow();

        // 4. Bookmark 생성 및 저장
        Bookmark bookmark = Bookmark.builder()
                .board(boardPS)
                .user(testUser)
                .build();
        bookmarkRepository.BookmarkSave(bookmark);

        // 안전하게 필드별 출력
        System.out.println(">>> Bookmark 저장 완료");
        System.out.println("BookmarkId: " + bookmark.getId());
        System.out.println("BoardId: " + bookmark.getBoard().getId());
        System.out.println("BoardTitle: " + bookmark.getBoard().getTitle());
        System.out.println("UserId: " + bookmark.getUser().getId());
        System.out.println("Username: " + bookmark.getUser().getUsername());
    }

    // 북마크 삭제
    @Test
    public void bookmarkDelete_test() {
        // 1. 테스트용 User 생성 및 저장
        User testUser = User.builder()
                .username("tester")
                .build();
        userRepository.save(testUser);

        // 2. 테스트용 Board 생성 및 User 연결 후 저장
        Board testBoard = Board.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .user(testUser)
                .build();
        boardRepository.save(testBoard);

        // 3. Bookmark 생성 및 저장
        Bookmark bookmark = Bookmark.builder()
                .board(testBoard)
                .user(testUser)
                .build();
        bookmarkRepository.BookmarkSave(bookmark);

        // 4. 삭제
        bookmarkRepository.BookmarkDelete(testBoard.getId(), testUser.getId());

        // 5. 삭제 여부 확인
        bookmarkRepository.findByBoardIdAndUserId(testBoard.getId(), testUser.getId())
                .ifPresentOrElse(
                        b -> System.out.println(">>> 삭제 후 북마크 존재: BoardId = " + b.getBoard().getId()),
                        () -> System.out.println(">>> 삭제 후 북마크 없음") // false 예상
                );
    }

    @Test
    @DisplayName("북마크 리스트 조회 - DTO 변환 포함, Board 모든 값 출력")
    public void bookmarkList_test() {
        // 1. 테스트용 User 생성 및 저장
        User testUser = User.builder().username("tester").build();
        userRepository.save(testUser);
        System.out.println(">>> Test User: " + testUser.getId() + ", " + testUser.getUsername());

        //카데고리 만들기
        Category testCategory = Category.builder()
                .category("테스트 카테고리")
                .build();
        categoryRepository.save(testCategory);
        System.out.println(">>> Test Category: " + testCategory.getCategory() + ", " + testCategory.getId());

        // 2. Boards 생성 및 북마크 생성
        List<Board> savedBoards = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Board board = Board.builder()
                    .title("테스트 제목 " + i)
                    .content("테스트 내용 " + i)
                    .views(10 + i)
                    .user(testUser)
                    .category(testCategory)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            boardRepository.save(board);
            savedBoards.add(board);

            Bookmark bookmark = Bookmark.builder()
                    .board(board)
                    .user(testUser)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            bookmarkRepository.BookmarkSave(bookmark);

            System.out.printf(">>> Board %d 저장, Bookmark 생성 완료%n", board.getId());
        }


        // 3. 서비스 레벨에서 DTO 변환 후 조회
        BookmarkResponse.BookmarkListDTO respDTO = bookmarkService.bookmarkList(testUser.getId());

        // 4. DTO 값 출력
        System.out.println(">>> BookmarkListDTO 출력:");
        System.out.println("isList: " + respDTO.isList());
        for (BookmarkResponse.BookmarkDTO dto : respDTO.getBookmarksList()) {
            System.out.println("===== BookmarkDTO =====");
            System.out.println("BookmarkId: " + dto.getBookId());
            System.out.println("CreatedAt: " + dto.getCreateAt());
            BoardResponse.DTO boardDTO = dto.getBoard();
            if (boardDTO != null) {
                System.out.println("BoardId: " + boardDTO.getId());
                System.out.println("Board Title: " + boardDTO.getTitle());
                System.out.println("Board Content: " + boardDTO.getContent());
                System.out.println("Board Views: " + boardDTO.getViews());
                System.out.println("Board BookmarkCount: " + boardDTO.getBookmarkCount());
                System.out.println("Board Category: " + boardDTO.getCategory());
                System.out.println("Board CreatedAt: " + boardDTO.getCreateAt());
                System.out.println("Board UpdatedAt: " + boardDTO.getUpdateAt());
            }


            System.out.println("======================");
        }
    }
}
