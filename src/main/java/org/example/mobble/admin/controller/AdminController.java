package org.example.mobble.admin.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.mobble._util.error.ex.Exception401;
import org.example.mobble._util.util.Resp;
import org.example.mobble.admin.dto.AdminRequest;
import org.example.mobble.admin.dto.AdminResponse;
import org.example.mobble.admin.service.AdminService;
import org.example.mobble.report.domain.ReportStatus;
import org.example.mobble.report.dto.ReportRequest;
import org.example.mobble.report.dto.ReportResponse;
import org.example.mobble.report.service.ReportService;
import org.example.mobble.user.domain.User;
import org.example.mobble.user.dto.UserRequest;
import org.example.mobble.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class AdminController {
    private final AdminService adminService;
    private final HttpSession session;

    //관리자 전체 신고 리스트
    @GetMapping("/admin/reports")
    public String getAdminReportList(Model model) {
        List<AdminResponse.ReportDTO> resDTO = adminService.getList();
        model.addAttribute("model", resDTO);
        return "admin/report-page";
    }

    //관리자 상태 변경
    @PostMapping("/admin/reports/{id}/update")
    public String statusUpdate(@PathVariable(name = "id") Integer reportId, AdminRequest.ReportUpateDTO reqDTO) {
        ReportStatus status = adminService.updateStatus(reportId,reqDTO);
        return "admin/report-page";
    }

    // 관리자 신고 상세보기 (모달)
    @ResponseBody
    @GetMapping("/admin/reports/{id}")
    public  ResponseEntity<?> getAdminReport(@PathVariable(name = "id") Integer reportId, Model model) {
        AdminResponse.ReportDetailDTO resDTO = adminService.getReport(reportId);
        return Resp.ok(resDTO);
    }

    // 관리자 로그인
    @GetMapping("/admin/login-form")
    public String loginForm() {
        return "admin/main";
    }

    @PostMapping("/admin/login")
    public String adminLogin(AdminRequest.LoginDTO reqDTO) {
        User user = adminService.findUsername(reqDTO);
        session.setAttribute("user", user);
        return "admin/report-page";
    }

    @GetMapping("/admin/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/admin/login-form";
    }

}
