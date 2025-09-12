package org.example.mobble.bookmark.controller;

import org.example.mobble.board.domain.Board;
import org.example.mobble.board.domain.BoardRepository;
import org.example.mobble.board.dto.BoardResponse;
import org.example.mobble.bookmark.domain.Bookmark;
import org.example.mobble.bookmark.domain.BookmarkRepository;
import org.example.mobble.bookmark.dto.BookmarkResponse;
import org.example.mobble.bookmark.service.BookmarkService;
import org.example.mobble.category.domain.Category;
import org.example.mobble.category.domain.CategoryRepository;
import org.example.mobble.user.domain.User;
import org.example.mobble.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

    private User testUser;
    private Board testBoard;

    @BeforeEach
    void setUp() {
        // 1. 테스트용 유저 생성
        testUser = User.builder()
                .username("testuser")
                .password("1234")
                .email("test@test.com")
                .role("user")
                .build();
        userRepository.save(testUser);

        // 2. 테스트용 Board 생성 및 User 연결
        testBoard = Board.builder()
                .title("테스트 게시글")
                .content("테스트 내용")
                .user(testUser)  // User 연결
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();
        boardRepository.save(testBoard);
    }

    @Test
    @DisplayName("북마크 저장 성공 - Security 없이 세션 기반")
    void bookmarkSave_success_noSecurity() throws Exception {
        // 세션에 testUser 넣고 요청
        mockMvc.perform(post("/bookmark/{boardId}/save", testBoard.getId())
                        .sessionAttr("user", testUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))  // 숫자로 비교
                .andExpect(jsonPath("$.msg").value("성공")); // 메시지도 확인

        // DB에 북마크 저장 확인
        Bookmark bookmark = bookmarkRepository.findByBoardIdAndUserId(
                testBoard.getId(), testUser.getId()
        ).orElseThrow(() -> new IllegalStateException("북마크 저장 실패"));

        System.out.println("===== Bookmark 저장 확인 =====");
        System.out.println("BookmarkBoardId: " + bookmark.getBoard().getId());
        System.out.println("BookmarkUserId: " + bookmark.getUser().getId());
        System.out.println("Bookmark CreatedAt: " + bookmark.getCreatedAt());
        System.out.println("======================");

        assertThat(bookmark.getBoard().getId()).isEqualTo(testBoard.getId());
        assertThat(bookmark.getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("북마크 삭제 성공")
    void bookmarkDelete_success() throws Exception {
        // 1. 북마크 생성
        Bookmark bookmark = Bookmark.builder()
                .board(testBoard)
                .user(testUser)
                .build();
        bookmarkRepository.BookmarkSave(bookmark);

        // ✅ 삭제 전 전체 값 출력
        System.out.println("===== 삭제 전 Bookmark =====");
        System.out.println("BookmarkId: " + bookmark.getId());
        System.out.println("Bookmark CreatedAt: " + bookmark.getCreatedAt());
        System.out.println("======================");

        // 2. 삭제 API 호출 (JSON 응답)
        mockMvc.perform(post("/bookmark/{boardId}/delete", testBoard.getId())
                        .sessionAttr("user", testUser))
                .andExpect(status().isOk()) // 200 OK 확인
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"));

        // 3. 삭제 여부 확인
        boolean exists = bookmarkRepository.findByBoardIdAndUserId(testBoard.getId(), testUser.getId()).isPresent();

        System.out.println("===== 삭제 후 Bookmark 확인 =====");
        System.out.println("삭제 후 존재 여부: " + exists);
        System.out.println("===============================");

        assertThat(exists).isFalse();
    }



    @Test
    @DisplayName("북마크 리스트 조회 - 더미 5개, Board 모든 값 포함")
    void bookmarkList_withBoardTitle_success() throws Exception {
        // 1. 테스트용 User 생성 및 저장
        User testUser = User.builder()
                .username("tester")
                .build();
        userRepository.save(testUser);
        System.out.println(">>> Test User: " + testUser.getId() + ", " + testUser.getUsername());

        // 2. 테스트용 Category 생성 및 저장
        Category testCategory = Category.builder()
                .category("테스트 카테고리")
                .build();
        categoryRepository.save(testCategory);
        System.out.println(">>> Test Category: " + testCategory.getId() + ", " + testCategory.getCategory());

        // 3. Boards 생성 및 북마크 생성
        List<Board> boards = new ArrayList<>();
        for (int i = 1; i <= 35; i++) {
            int randomViews = ThreadLocalRandom.current().nextInt(0, 1000); // 0~999 랜덤 조회수

            Board board = Board.builder()
                    .title("테스트 제목 " + i)
                    .content("테스트 내용 " + i)
                    .views(randomViews)
                    .user(testUser)
                    .category(testCategory)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            boardRepository.save(board);
            boards.add(board);

            System.out.printf(">>> Board %d (views=%d) 생성 완료%n", board.getId(), randomViews);
        }

// 4. 북마크는 랜덤으로 20개만 생성
        Collections.shuffle(boards); // Board 리스트 섞기
        List<Board> bookmarkTargets = boards.subList(0, 20); // 앞에서 20개 선택

        for (Board board : bookmarkTargets) {
            Bookmark bm = Bookmark.builder()
                    .board(board)
                    .user(testUser)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            bookmarkRepository.BookmarkSave(bm);
            System.out.printf(">>> Board %d에 Bookmark 생성%n", board.getId());
        }

        // 4. 북마크 리스트 조회 API 요청 (MockMvc)
        mockMvc.perform(get("/bookmarks")
                        .sessionAttr("user", testUser)
                        .param("sort", "bookCount_DESC")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/bookmark/list-page"));

        // 5. 서비스 레벨에서 DTO 변환 후 조회
        BookmarkResponse.BookmarkListDTO respDTO = bookmarkService.bookmarkList(testUser.getId(), "bookCount_DESC", 1, 10);

        // 6. DTO 값 출력
        System.out.println("===== 북마크 리스트 조회 결과 =====");
        System.out.println("isList = " + respDTO.isList());
        System.out.println("Prev = " + respDTO.getPrev());
        System.out.println("next = " + respDTO.getNext());
        System.out.println("totalCount = " + respDTO.getTotalCount());
        System.out.println("currentPage = " + respDTO.getCurrent());
        System.out.println("isFirst = " + respDTO.getIsFirst());
        System.out.println("isLast = " + respDTO.getIsLast());
        System.out.println("totalPage = " + respDTO.getTotalPage());
        for (BookmarkResponse.BookmarkDTO dto : respDTO.getBookmarksList()) {
            BoardResponse.DTO boardDto = dto.getBoard();
            System.out.println(">>> BookmarkDTO");
            System.out.println("BookmarkId: " + dto.getBookId());
            System.out.println("BoardId: " + boardDto.getId());
            System.out.println("Board Title: " + boardDto.getTitle());
            System.out.println("Board Content: " + boardDto.getContent());
            System.out.println("Board Views: " + boardDto.getViews());
            System.out.println("Bookmark Count: " + boardDto.getBookmarkCount());
            System.out.println("Board Category: " + boardDto.getCategory());
            System.out.println("Board CreatedAt: " + boardDto.getCreateAt());
            System.out.println("Board UpdatedAt: " + boardDto.getUpdateAt());
            System.out.println("Board Image: " + boardDto.getImage());
            System.out.println("Board UserName: " + boardDto.getUsername());
            System.out.println("===============================");
        }
    }

}
