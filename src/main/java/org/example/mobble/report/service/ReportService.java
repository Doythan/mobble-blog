package org.example.mobble.report.service;

import lombok.RequiredArgsConstructor;
import org.example.mobble._util.error.ex.Exception403;
import org.example.mobble._util.error.ex.Exception404;
import org.example.mobble.report.domain.Report;
import org.example.mobble.report.domain.ReportRepository;
import org.example.mobble.report.domain.ReportStatus;
import org.example.mobble.report.dto.ReportRequest;
import org.example.mobble.report.dto.ReportResponse;
import org.example.mobble.user.domain.User;
import org.example.mobble.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.example.mobble._util.error.ErrorEnum.*;

@RequiredArgsConstructor
@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;


    //내 신고 글 삭제
    @Transactional
    public void delete(Integer reportId, User user) {
        //유저 정보 조회
        User userPS = getUser(user.getId());
        //신고 권한이 있는지 확인
        Report reportPS = checkPermissions(reportId, userPS);
        //삭제
        reportRepository.delete(reportPS);

    }

    //내 신고 글 수정
    @Transactional
    public ReportResponse.ReportUpateDTO update(Integer reportId, User user, ReportRequest.ReportUpateDTO reqDTO) {
        //유저 정보 조회
        User userPS = getUser(user.getId());
        //신고 권한이 있는지 확인
        Report reportPS = checkPermissions(reportId, userPS);
        //업데이트
        reportPS.updateInfo(reqDTO);
        return new ReportResponse.ReportUpateDTO(reportPS);

    }

    //내 신고 글 전체 조회
    public List<ReportResponse.ReportDTO> getList(User user) {

        //유저 정보 조회 + 검증
        User userPS = getUser(user.getId());

        List<Report> reportList = reportRepository.findAllByUserId(user.getId());

        return reportList.stream()
                .map(ReportResponse.ReportDTO::new)
                .toList();
    }

    // 내 신고 글 보기
    public ReportResponse.ReportDetailDTO getReport(Integer reportId, User user) {
        //유저 정보 조회
        User userPS = getUser(user.getId());
        //신고 권한이 있는지 확인 + 조회
        Report reportPS = checkPermissions(reportId, userPS);

        return new ReportResponse.ReportDetailDTO(reportPS);
    }

    //권한 검증
    private Report checkPermissions(Integer reportId, User userPS) {
        Report reportPS = reportRepository.findById(reportId)
                .orElseThrow(() -> new Exception404(NOT_FOUND_REPORT));
        if (!userPS.getId().equals(reportPS.getUser().getId())) {
            throw new Exception403(FORBIDDEN_USER_AT_REPORT);
        }
        return reportPS;
    }

    //유저 조회
    private User getUser(Integer userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new Exception404(NOT_FOUND_USER_TO_USERID)
        );
    }

    @Transactional
    public void deleteReportPerWeek() {
        reportRepository.deleteByStatus(ReportStatus.COMPLETED);
    }
}
