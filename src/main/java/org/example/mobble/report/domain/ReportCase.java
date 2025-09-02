package org.example.mobble.report.domain;

// 케이스 추가 필요 (총 8개 정도면 좋을 듯)
public enum ReportCase {
    INAPPROPRIATE_AUTHOR_NAME("부적절한 작성자명"), INAPPROPRIATE_BOARD_CONTENT("부적절한 글 내용"), ADVERTISING_BOARD_CONTENT("광고성 글 내용"), ETC("기타");

    private String reason;

    ReportCase(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
