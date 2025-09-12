package org.example.mobble.report.domain;

public enum ReportStatus {
    PENDING("처리대기"),
    COMPLETED("처리완료"),
    REJECTED("신고반려");
    private final String label;

    ReportStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}