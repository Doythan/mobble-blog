package org.example.mobble.admin.dto;

import lombok.Data;
import org.example.mobble.report.domain.ReportStatus;

public class AdminRequest {
    @Data
    public static class ReportUpateDTO{

    private ReportStatus status;
    }

    @Data
    public class LoginDTO {
        private String authId;
        private String authPw;
    }

}
