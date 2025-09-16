package org.example.mobble.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mobble.report.service.ReportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportScheduler {
    private final ReportService reportService;

    // 매주 일요일 정오 (12:00 PM)에 처리 완료된 신고 자동 삭제
    @Scheduled(cron = "0 0 12 ? * SUN", zone = "Asia/Seoul")
    public void autoDeleteReport() {
        reportService.deleteReportPerWeek();
        log.info("처리 완료된 신고를 삭제했습니다.");
    }
}
