package org.example.mobble.admin.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
    @BeforeEach
    public void setup() {
    }

    //어드민 신고 전체 리스트
    @Test
    void get_admin_report_list_test() throws Exception {
        // given
        // when
        ResultActions actions = mvc.perform(
                get("/admin/reports")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );
            // then
            MvcResult result = actions.andExpect(status().isOk())
                    .andReturn();

            Map<String, Object> modelMap = result.getModelAndView().getModel();
            Object model = modelMap.get("model");
            System.out.println("=== model ===");
            System.out.println(model);
    }
    //신고 상태값 변경
        @Test
        void status_update_test() throws Exception {
                // given
            Integer reportId = 2;

            // when
                ResultActions actions = mvc.perform(
                        post("/admin/reports/{id}/update",reportId)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("status","COMPLETED")
                );
                // then
                actions.andExpect(status().isOk())
                        .andExpect(view().name("admin/report-page"))
                .andDo(print());
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