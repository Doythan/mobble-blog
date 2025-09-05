package org.example.mobble.report.domain;

public enum ReportStatus {
    PENDING("처리 전"),
    PROCESSING("처리 중"),
    COMPLETED("처리완료");

    private final String label;

    ReportStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}