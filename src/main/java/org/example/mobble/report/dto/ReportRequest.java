package org.example.mobble.report.dto;

import lombok.Data;
import org.example.mobble.report.domain.ReportCase;
import org.example.mobble.report.domain.ReportStatus;

public class ReportRequest {

    @Data
    public static  class ReportUpateDTO{

        private ReportCase result;
        private String resultEtc;
        private String content;
    }

}
