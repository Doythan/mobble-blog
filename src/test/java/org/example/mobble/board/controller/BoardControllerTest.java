package org.example.mobble.board.controller;

import org.example.mobble.board.TestUtils;
import org.example.mobble.board.dto.BoardResponse;
import org.example.mobble.user.domain.User;
import org.example.mobble.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
// 필요 시 테스트 데이터 주입
// @Sql(scripts = {"classpath:sql/cleanup.sql", "classpath:sql/fixtures.sql"})
class BoardControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        // 테스트용 로그인 세션
        User login = userRepository.findById(1).orElseThrow(); // fixture에 id=1 존재 필요
        session = new MockHttpSession();
        session.setAttribute("user", login);
    }

    @Test
    @DisplayName("목록: 첫 페이지 OK (model.pageDTO.isFirst=true)")
    void list_ok() throws Exception {
        MvcResult result = mockMvc.perform(get("/boards").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("board/list-page"))
                .andExpect(request().attribute("model", notNullValue()))
                .andReturn();

        TestUtils.printRequestAttributesAsJson(result);

        BoardResponse.mainListDTO model =
                (BoardResponse.mainListDTO) result.getRequest().getAttribute("model");
        assertThat(model).isNotNull();
        assertThat(model.getPageDTO().getIsFirst()).isTrue();
        // isLast는 데이터 개수에 따라 달라져서 고정 검증 생략
    }

    @Test
    @DisplayName("상세: 존재하는 게시글 OK (model 존재)")
    void detail_ok() throws Exception {
        MvcResult result = mockMvc.perform(get("/boards/{id}", 1).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("board/detail-page"))
                .andExpect(request().attribute("model", notNullValue()))
                .andReturn();

        TestUtils.printRequestAttributesAsJson(result);
        Object model = result.getRequest().getAttribute("model");
        assertThat(model).isInstanceOf(BoardResponse.DetailDTO.class);
    }

    @Test
    @DisplayName("검색: 기본 검색 OK (model 존재)")
    void search_default_ok() throws Exception {
        MvcResult result = mockMvc.perform(get("/boards/search")
                        .param("keyword", "제목")
                        .param("order", "CREATED_AT_DESC")
                        .param("page", "1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("board/list-page"))
                .andExpect(request().attribute("model", notNullValue()))
                .andReturn();

        TestUtils.printRequestAttributesAsJson(result);
    }

    @Test
    @DisplayName("저장: 리다이렉트 OK (/boards/{id})")
    void save_redirect_ok() throws Exception {
        mockMvc.perform(post("/boards")
                                .param("title", "새 제목")
                                .param("content", "새 내용")
                                .param("category", "temp-cat")
                                .session(session)
                        // .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/boards/*"));
    }

    @Test
    @DisplayName("수정: 소유자 수정 리다이렉트 OK")
    void update_redirect_ok() throws Exception {
        Integer targetId = 4;
        mockMvc.perform(post("/boards/{id}/update", targetId)
                                .param("title", "제목1-수정")
                                .param("content", "내용1-수정")
                                .session(session)
                        // .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + targetId));
    }

    @Test
    @DisplayName("신고: 리다이렉트 OK")
    void report_redirect_ok() throws Exception {
        mockMvc.perform(post("/boards/{id}/report", 1)
                                // ⚠️ ReportCase가 enum이면 '라벨'이 아니라 '상수명'을 보내야 합니다.
                                // 예) .param("result", "SPAM")
                                .param("result", "SPAM")        // 실제 enum 이름으로 교체
                                .param("content", "스팸 신고")
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

    @Test
    @DisplayName("myfeed 목록: 첫 페이지 OK (model.pageDTO.isFirst=true)")
    void myfeed_list_ok() throws Exception {
        MvcResult result = mockMvc.perform(get("/boards/me")
                        .param("order", "VIEW_COUNT_DESC")
                        .param("page", "1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("board/myfeed-page"))
                .andExpect(request().attribute("model", notNullValue()))
                .andReturn();

        TestUtils.printRequestAttributesAsJson(result);

        BoardResponse.mainListDTO model =
                (BoardResponse.mainListDTO) result.getRequest().getAttribute("model");
        assertThat(model.getPageDTO().getIsFirst()).isTrue();
    }
}
