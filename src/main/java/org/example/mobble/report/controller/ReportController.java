package org.example.mobble.report.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.mobble.report.service.ReportService;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ReportController {
    private final ReportService reportService;
    private final HttpSession session;
}
