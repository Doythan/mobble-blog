package org.example.mobble.report.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.mobble._util.error.ex.Exception401;
import org.example.mobble._util.util.Resp;
import org.example.mobble.board.dto.BoardRequest;
import org.example.mobble.report.domain.ReportStatus;
import org.example.mobble.report.dto.ReportRequest;
import org.example.mobble.report.dto.ReportResponse;
import org.example.mobble.report.service.ReportService;
import org.example.mobble.user.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.example.mobble._util.error.ErrorEnum.UNAUTHORIZED_NO_EXISTS_USER_INFO;

@RequiredArgsConstructor
@Controller
public class ReportController {
    private final ReportService reportService;
    private final HttpSession session;

    //신고 수정 form 가기 (모달)
    @ResponseBody
    @GetMapping ("/reports/{id}/update-form")
    public ResponseEntity<?>  updateForm(@PathVariable(name = "id") Integer reportId) {
        User user = getLoginUser();
        //조회
        ReportResponse.ReportDetailDTO resDTO = reportService.getReport(reportId,user);
        return Resp.ok(resDTO);
    }

    //내 신고 리스트
    @GetMapping ("/reports")
    public String getReportList(Model model) throws JsonProcessingException {
        User user = getLoginUser();
        //조회
        List<ReportResponse.ReportDTO> resDTO = reportService.getList(user);

        model.addAttribute("reportsJson", new ObjectMapper().writeValueAsString(resDTO));
        return "mypage/report/list-page";
    }

    //내 신고 보기(모달)
    @ResponseBody
    @GetMapping ("/reports/{id}")
    public ResponseEntity<?> getReport(@PathVariable(name = "id") Integer reportId) {
        User user = getLoginUser();
        //조회
        ReportResponse.ReportDetailDTO resDTO = reportService.getReport(reportId,user);
        return Resp.ok(resDTO);
    }

    //내 신고 삭제
    @ResponseBody
    @PostMapping ("/reports/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Integer reportId) {
        User user = getLoginUser();
        //삭제
        reportService.delete(reportId,user);
        return Resp.ok(null);
    }

    //내 신고 수정
    @ResponseBody
    @PostMapping ("/reports/{id}/update")
    public ResponseEntity<?> update(@PathVariable(name = "id") Integer reportId,
                                    @RequestBody ReportRequest.ReportUpateDTO reqDTO) {
        User user = getLoginUser();
        //수정
        ReportResponse.ReportUpateDTO resDTO = reportService.update(reportId,user,reqDTO);
        return Resp.ok(resDTO);
    }

    //서비스 이용자 확인 절차
    private User getLoginUser() {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new Exception401(UNAUTHORIZED_NO_EXISTS_USER_INFO); // 에러 코드, 에러 메시지 컨벤션
        }
        return user;
    }

}
