package org.example.mobble.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ReportController {
    private final ReportService reportService;
}
