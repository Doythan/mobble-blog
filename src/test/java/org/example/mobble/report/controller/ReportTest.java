package org.example.mobble.report.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mobble.report.domain.ReportCase;
import org.example.mobble.report.dto.ReportRequest;
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

import java.util.List;
import java.util.Map;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)

public class ReportTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om = new ObjectMapper();
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
                // when
                ResultActions actions = mvc.perform(
                        get("/reports")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .session(session)
                );

                // then
            MvcResult result = actions
                    .andExpect(status().isOk())
                    .andExpect(view().name("mypage/report/list-page"))
                    .andExpect(model().attributeExists("reportsJson"))
                    .andReturn();
            String reportsJson = (String) result.getModelAndView().getModel().get("reportsJson");
            System.out.println("✅응답바디 : "  + reportsJson);


            //json 형태를 model에 담기때문에 기존  jsonpath 사용 불가 model 확인도 불가 => 파싱 해서 확인 !
            ObjectMapper om = new ObjectMapper();
            List<Map<String, Object>> list = om.readValue(reportsJson, new TypeReference<>() {});

            // 첫 번째 JSON 객체
            Map<String, Object> first = list.get(0);

            // ✅ 필드 값 전부 검증
            assertThat(first.get("id")).isEqualTo(1);
            assertThat(first.get("result")).isEqualTo("ADVERTISING_BOARD_CONTENT");
            assertThat(first.get("resultEtc")).isNull();
            assertThat(first.get("status")).isEqualTo("PENDING");
            assertThat(first.get("content")).isEqualTo("광고성 글이라 신고합니다.");
            assertThat(first.get("boardTitle")).isEqualTo("제목2");


        }
    //상세보기 테스트
        @Test
        void get_report_test() throws Exception {
                // given
                Integer reportId = 1;
                // when
                ResultActions actions = mvc.perform(
                        get("/reports/{id}",reportId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(session)
                );
                // then
            String responseBody = actions.andReturn().getResponse().getContentAsString();
            System.out.println("✅응답바디 : " + responseBody);

        }


    //수정 테스트
        @Test
        void update_test() throws Exception {
            // given
            Integer reportId = 1;
            ReportRequest.ReportUpateDTO reqDTO = new ReportRequest.ReportUpateDTO();
            reqDTO.setResult(ReportCase.valueOf("ADVERTISING_BOARD_CONTENT"));
            reqDTO.setContent("내용 수정이요");

            String requestBody = om.writeValueAsString(reqDTO);
            System.out.println("✅응답바디 : " + requestBody);
                // when
                ResultActions actions = mvc.perform(
                        post("/reports/{id}/update",reportId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(session)
                                .content(requestBody)
                );
                // then
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.msg").value("성공"))
                    .andExpect(jsonPath("$.body.id").value(1))
                    .andExpect(jsonPath("$.body.result").value("ADVERTISING_BOARD_CONTENT"))
                    .andExpect(jsonPath("$.body.resultEtc").isEmpty())
                    .andExpect(jsonPath("$.body.content").value("내용 수정이요"));

    }
    //삭제 테스트
        @Test
        void delete_test() throws Exception {
            // given
            Integer reportId = 1;
                // when
                ResultActions actions = mvc.perform(
                        post("/reports/{id}/delete",reportId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(session)
                );
            // then
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.msg").value("성공"))
                    .andExpect(jsonPath("$.body").isEmpty());
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