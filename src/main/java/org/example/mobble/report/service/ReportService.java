package org.example.mobble.report.service;

import lombok.RequiredArgsConstructor;
import org.example.mobble.report.domain.ReportRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReportService {
    private final ReportRepository reportRepository;
}
