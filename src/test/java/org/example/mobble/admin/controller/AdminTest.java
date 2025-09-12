package org.example.mobble.admin.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mobble.admin.dto.AdminRequest;
import org.example.mobble.report.domain.ReportCase;
import org.example.mobble.report.domain.ReportStatus;
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

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AdminTest {

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
    //어드민 신고 전체 리스트
    @Test
    void get_admin_report_list_test() throws Exception {
        // when
        ResultActions actions = mvc.perform(
                get("/admin/reports")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );

        // then
        MvcResult result = actions.andExpect(status().isOk())
                .andExpect(view().name("admin/report-page"))   // 뷰 이름 확인
                .andExpect(model().attributeExists("reportsJson")) // 모델에 reportsJson 있는지 확인
                .andReturn();

        // 실제 값 출력 확인 (선택)
        String reportsJson = (String) result.getModelAndView().getModel().get("reportsJson");
        System.out.println("✅ reportsJson = " + reportsJson);

    }
    //신고 상태값 변경
    @Test
    void status_update_test() throws Exception {
        // given
        Integer reportId = 2;
        AdminRequest.ReportUpateDTO reqDTO = new AdminRequest.ReportUpateDTO();
        reqDTO.setStatus(ReportStatus.REJECTED); // 상태 변경 테스트용

        String requestBody = om.writeValueAsString(reqDTO);

        // when
        ResultActions actions = mvc.perform(
                post("/admin/reports/{id}/update", reportId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(requestBody)
        );

        // then
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println("✅응답바디 : " + responseBody);

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body").value("PROCESSING"));
    }

//어드민 신고 상세보기
        @Test
        public void get_admin_report_test() throws Exception {
                //given
                Integer reportId = 4;

                //when
                ResultActions actions = mvc.perform(
                        MockMvcRequestBuilders
                                .get("/admin/reports/{id}", reportId)
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //eye
                String responseBody = actions.andReturn().getResponse().getContentAsString();
                System.out.println("✅응답바디 : " + responseBody);


        }


}