package org.example.mobble.board.controller;

import org.example.mobble.user.domain.User;
import org.example.mobble.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        // H2 인메모리 + MySQL 호환 모드 (DATE_SUB 등 사용 가능)
        "spring.datasource.url=jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.jpa.open-in-view=false"
})
// 더미 주입: FK 고려해서 카테고리 → 유저 → 보드 순서
@Sql(value = {"/db/category-data.sql", "/db/user-data.sql", "/db/board-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class BoardControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        // 로그인 세션 생성 (컨트롤러는 session "user"만 확인)
        // 더미에 user id=1이 존재한다고 가정
        User login = userRepository.findById(1).orElseThrow();
        session = new MockHttpSession();
        session.setAttribute("user", login);
    }

    @Test
    @DisplayName("목록: 첫 페이지 OK")
    void list_ok() throws Exception {
        mockMvc.perform(get("/boards").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("board/list-page"))
                .andExpect(request().attribute("model", new Object()))
                .andExpect(request().attribute("isFirst", true));
        // isLast는 더미 개수/ PER_PAGE에 따라 달라짐 → 고정 검증은 생략
    }

    @Test
    @DisplayName("상세: 존재하는 게시글 OK")
    void detail_ok() throws Exception {
        // 더미에 id=1 게시글이 있다고 가정
        mockMvc.perform(get("/boards/{id}", 1).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("board/detail-page"))
                .andExpect(request().attribute("model", new Object()));
    }

    @Test
    @DisplayName("검색: 본문/제목 기본 검색 OK")
    void search_default_ok() throws Exception {
        mockMvc.perform(get("/boards/search")
                        .param("keyword", "제목") // 기본 검색 (접두사 없이)
                        .param("order", "CREATED_AT_DESC")
                        .param("page", "1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("board/list-page"))
                .andExpect(request().attribute("model", new Object()));
    }

    @Test
    @DisplayName("저장: 리다이렉트 OK")
    void save_redirect_ok() throws Exception {
        mockMvc.perform(post("/boards")
                                .param("title", "새 제목")
                                .param("content", "새 내용")
                                .param("category", "temp-cat")
                                .session(session)
                        // .with(csrf()) // Spring Security CSRF 활성 시 주석 해제
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", matchesPattern(".*/boards/\\d+")));
    }

    @Test
    @DisplayName("수정: 소유자 수정 리다이렉트 OK")
    void update_redirect_ok() throws Exception {
        // 더미에서 id=1 게시글의 userId가 1이라고 가정(세션 유저와 동일)
        mockMvc.perform(post("/boards/{id}/update", 1)
                                .param("id", "1")
                                .param("title", "제목1-수정")
                                .param("content", "내용1-수정")
                                .session(session)
                        // .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/1"));
    }

    @Test
    @DisplayName("신고: 리다이렉트 OK")
    void report_redirect_ok() throws Exception {
        mockMvc.perform(post("/boards/{id}/report", 1)
                                .param("result", "SPAM")        // ReportCase 값 중 하나
                                .param("content", "스팸 신고")    // 신고 내용
                                .session(session)
                        // .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/1"));
    }

    @Test
    @DisplayName("삭제: 리다이렉트 OK")
    void delete_redirect_ok() throws Exception {
        mockMvc.perform(post("/boards/{id}/delete", 1)
                                .session(session)
                        // .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards"));
    }
}