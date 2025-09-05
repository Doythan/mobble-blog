package org.example.mobble.report.controller;


import org.example.mobble.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)

public class ReportTest {

    @Autowired
    private MockMvc mvc;

    private MockHttpSession session;

    @BeforeEach
    public void setup() {
        // 세션 객체 생성
        session = new MockHttpSession();
        // User 엔티티 가짜 객체 생성
        User user = User.builder()
                .id(1)
                .username("ssar")
                .password("1234")
                .email("ssar@nate.com")
                .build();
            session.setAttribute("user", user);
    }

        @Test
        public void update_form_test() throws Exception {
                //given
                Integer reportId = 1;

                //when
                ResultActions actions = mvc.perform(
                        MockMvcRequestBuilders
                                .get("/reports/{id}/update-form", reportId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(session)
                );

                //eye
                String responseBody = actions.andReturn().getResponse().getContentAsString();
                System.out.println("✅응답바디 : " + responseBody);

                // then

        }

    //전체 리스트 보기
        @Test
        void get_report_list_test() throws Exception {
                // given
                // when
                ResultActions actions = mvc.perform(
                        get("/reports")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .session(session)
                );
                // then
                MvcResult result = actions.andExpect(status().isOk())
                        .andReturn();

                Map<String, Object> modelMap = result.getModelAndView().getModel();
                Object model = modelMap.get("model");
                System.out.println("=== model ===");
                System.out.println(model);

        }
    //상세보기 테스트
        @Test
        void get_report_test() throws Exception {
                // given
                Integer reportId = 1;
                // when
                ResultActions actions = mvc.perform(
                        get("/reports/{id}",reportId)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .session(session)
                );
                // then
                MvcResult result = actions.andExpect(status().isOk())
                        .andReturn();

                Map<String, Object> modelMap = result.getModelAndView().getModel();
                Object model = modelMap.get("model");
                System.out.println("=== model ===");
                System.out.println(model);

        }


    //수정 테스트
        @Test
        void update_test() throws Exception {
            // given
            Integer reportId = 1;
                // when
                ResultActions actions = mvc.perform(
                        post("/reports/{id}/update",reportId)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .session(session)
                                .param("result","ADVERTISING_BOARD_CONTENT")
                                .param("content","내용 수정이요")
                );
                // then
                actions.andExpect(status().is3xxRedirection()) // redirect
                .andExpect(redirectedUrlPattern("/reports*")) // 저장 후 리다이렉트
                        .andDo(print());
        }
    //삭제 테스트
        @Test
        void delete_test() throws Exception {
            // given
            Integer reportId = 1;
                // when
                ResultActions actions = mvc.perform(
                        post("/reports/{id}/delete",reportId)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .session(session)
                );
                // then
                actions.andExpect(status().is3xxRedirection()) // redirect
                        .andExpect(redirectedUrlPattern("/reports*")) // 저장 후 리다이렉트
                        .andDo(print());
        }

    //저장 테스트
    @Test
    void report_save_test() throws Exception {

        // given
        Integer boardId = 3;
        // when
        ResultActions actions = mvc.perform(
                post("/boards/{id}/report",boardId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session(session)
                        .param("result","ETC")
                        .param("content","추가 신고")
                        .param("resultEtc","추가 기타 사항")

        );
        // then
        actions.andExpect(status().is3xxRedirection()) // redirect
                .andExpect(redirectedUrlPattern("/boards/*")) // 저장 후 리다이렉트
                .andDo(print());
    }


}