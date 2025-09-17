package org.example.mobble.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.mobble._util.error.ErrorEnum;
import org.example.mobble._util.error.ex.Exception400;
import org.example.mobble._util.error.ex.Exception403;
import org.example.mobble._util.error.ex.Exception404;
import org.example.mobble.admin.dto.AdminRequest;
import org.example.mobble.admin.dto.AdminResponse;
import org.example.mobble.board.domain.Board;
import org.example.mobble.board.domain.BoardRepository;
import org.example.mobble.board.dto.BoardResponse;
import org.example.mobble.report.domain.Report;
import org.example.mobble.report.domain.ReportRepository;
import org.example.mobble.report.domain.ReportStatus;
import org.example.mobble.user.domain.User;
import org.example.mobble.user.domain.UserRepository;
import org.example.mobble.user.domain.UserStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
            Board boardPS = getBoard(report.getBoard().getId());
            resDTO.add(new AdminResponse.ReportDTO(boardPS, report));
        }
        return resDTO;
    }

    //board 조회
    public Board getBoard(Integer boardId) {
        return boardRepository.findById(boardId).orElseThrow(
                () -> new Exception400(NOT_FOUND_BOARD)
        );
    }

    // User 조회
    @Transactional(readOnly = true)
    public List<AdminResponse.UserDTO> getUser() {
        List<User> userList = userRepository.findAll();
        List<AdminResponse.UserDTO> resDTO = new ArrayList<>();
        for (User u : userList) {
            resDTO.add(new AdminResponse.UserDTO(u));
        }
        return resDTO;
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
        return new AdminResponse.ReportDetailDTO(reportPS);
    }

    // 닉네임 수정
    @Transactional
    public String updateUsername(Integer userId, AdminRequest.UsernameUpdateDTO reqDTO) {
        if (reqDTO == null || reqDTO.getNickname() == null) {
            throw new Exception400(BAD_REQUEST);
        }
        String newNickname = reqDTO.getNickname().trim();
        if (newNickname.isEmpty()) throw new Exception400(BAD_REQUEST);

        User user = userRepository.findById(userId).orElseThrow(() ->  new Exception404(NOT_FOUND_USER_TO_USERID));

        // 동일 값이면 패스
        if(!newNickname.equals(user.getUsername())) {
            // 중복 체크
            if (userRepository.existsByUsername(newNickname)) {
                throw new Exception400(BAD_REQUEST);  // 이미 존재하는 닉네임
            }
            // 실제 변경 (username을 닉네임으로 사용 중이라고 가정)
            user.updateUsername(newNickname);
        }
        return user.getUsername();
    }

    // 강제 탈퇴
    @Transactional
    public void forceDeleteUser(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->  new Exception404(NOT_FOUND_USER_TO_USERID));

        if (user.getStatus() == UserStatus.DELETED) return;
        user.delete();
    }

    @Transactional
    public User findUsername(AdminRequest.LoginDTO reqDTO) {
        // 유저있는지 조회
        User foundUser = userRepository.findActiveByUsername(reqDTO.getAuthId())
                .orElseThrow(() -> new Exception403(NOT_FOUND_USER_TO_USERNAME));

        // 비밀번호 맞는지 확인
        if (!bCryptPasswordEncoder.matches(reqDTO.getAuthPw(), foundUser.getPassword()))
            throw new Exception403(ErrorEnum.FORBIDDEN_NO_MATCH_PASSWORD);

        // role가 admin인지 확인
        if (!foundUser.getRole().equals("admin")) {
            throw new Exception403(FORBIDDEN_NOT_ADMIN);
        }
        return foundUser;
    }

    @Transactional
    public List<AdminResponse.BoardListDTO> getBoardList() {
        List<Board> foundBoardList = boardRepository.findboardList();
        List<AdminResponse.BoardListDTO> resDTO = new ArrayList<>();

        for (Board board : foundBoardList) {
            BoardResponse.DetailDTO boardPS = findByIdDetail(board.getId(),board.getUser().getId());
            resDTO.add(new AdminResponse.BoardListDTO(board,boardPS));
        }
        return resDTO;

    }
    public BoardResponse.DetailDTO findByIdDetail(Integer boardId,Integer userId) {
        return boardRepository.findByIdDetail(boardId,userId).orElseThrow(
                () -> new Exception400(NOT_FOUND_BOARD)
        );
    }

    @Transactional
    public void deleteBoard(Integer boardId) {
        boardRepository.delete(boardId);
    }
}
