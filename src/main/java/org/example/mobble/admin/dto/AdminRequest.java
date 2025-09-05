package org.example.mobble.admin.dto;

import lombok.Data;
import org.example.mobble.report.domain.ReportStatus;

public class AdminRequest {
    @Data
    public class ReportUpateDTO{

    private ReportStatus status;
}
}
