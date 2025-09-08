package org.example.mobble.report.domain;

// 케이스 추가 필요 (총 8개 정도면 좋을 듯)
public enum ReportCase {
    INAPPROPRIATE_AUTHOR_NAME("부적절한 작성자명"),
    INAPPROPRIATE_BOARD_CONTENT("부적절한 글 내용"),
    ADVERTISING_BOARD_CONTENT("광고성 글 내용"),
    COPYRIGHT_VIOLATION("저작권 침해"),
    PERSONAL_INFORMATION("개인정보 노출"),
    ABUSIVE_LANGUAGE("욕설/비방"),
    SPAM("도배/스팸"),
    ETC("기타");

    private String result;

    ReportCase() {
    }

    ReportCase(String result) {
        this.result = result;
    }

    public String getReason() {
        return result;
    }
}
