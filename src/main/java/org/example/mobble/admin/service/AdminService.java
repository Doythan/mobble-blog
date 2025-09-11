package org.example.mobble.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.mobble._util.error.ErrorEnum;
import org.example.mobble._util.error.ex.Exception400;
import org.example.mobble._util.error.ex.Exception401;
import org.example.mobble._util.error.ex.Exception403;
import org.example.mobble._util.error.ex.Exception404;
import org.example.mobble.admin.dto.AdminRequest;
import org.example.mobble.admin.dto.AdminResponse;
import org.example.mobble.board.domain.Board;
import org.example.mobble.board.domain.BoardRepository;
import org.example.mobble.report.domain.Report;
import org.example.mobble.report.domain.ReportRepository;
import org.example.mobble.report.domain.ReportStatus;
import org.example.mobble.user.domain.User;
import org.example.mobble.user.domain.UserRepository;
import org.example.mobble.user.dto.UserRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.example.mobble._util.error.ErrorEnum.*;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final ReportRepository reportRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;




    //전체 신고 리스트 보기
    public List<AdminResponse.ReportDTO> getList() {
        List<Report> reportList = reportRepository.findAll();
        List<AdminResponse.ReportDTO> resDTO = new ArrayList<>();
        for (Report report : reportList) {
          Board boardPS =   getBoard(report.getBoard().getId());
            resDTO.add(new AdminResponse.ReportDTO(boardPS,report));
        }
        return resDTO;
    }
    //board 조회
    public Board getBoard(Integer boardId) {
        return boardRepository.findById(boardId).orElseThrow(
                () -> new Exception400(NOT_FOUND_BOARD)
        );
    }
    //user 조회
    private User getUser(Integer userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new Exception404(NOT_FOUND_USER_TO_USERID)
        );
    }

    //상태 변경
    @Transactional
    public ReportStatus updateStatus(Integer reportId, AdminRequest.ReportUpateDTO reqDTO) {
        Report reportPS = reportRepository.findById(reportId)
                .orElseThrow(() -> new Exception404(NOT_FOUND_REPORT));

        reportPS.updateStauts(reqDTO.getStatus());
        return reportPS.getStatus();
    }
    //신고 글 상세보기
    public AdminResponse.ReportDetailDTO getReport(Integer reportId) {
        Report reportPS = reportRepository.findById(reportId)
                .orElseThrow(() -> new Exception404(NOT_FOUND_REPORT));
        return new AdminResponse.ReportDetailDTO (reportPS);
    }

    @Transactional
    public User findUsername(AdminRequest.LoginDTO reqDTO) {
        // 유저있는지 조회
       User foundUser = userRepository.findByUsername(reqDTO.getAuthId()).orElseThrow(() -> new Exception403(NOT_FOUND_USER_TO_USERNAME));

       // 비밀번호 맞는지 확인
        if (!bCryptPasswordEncoder.matches(reqDTO.getAuthPw(), foundUser.getPassword()))
            throw new Exception403(ErrorEnum.FORBIDDEN_NO_MATCH_PASSWORD);

       // role가 admin인지 확인
        if (!foundUser.getRole().equals("admin")) { throw new Exception403(FORBIDDEN_NOT_ADMIN);}
        return foundUser;
    }
}
