package org.example.mobble.admin.dto;

import lombok.Data;
import org.example.mobble.report.domain.ReportStatus;

public class AdminRequest {
    @Data
    public static class ReportUpateDTO{

    private ReportStatus status;
    }

    @Data
    public static class UsernameUpdateDTO {
        private String nickname; // 프론트에서 보낼 새 닉네임
    }

    @Data
    public class LoginDTO {
        private String authId;
        private String authPw;
    }

}
