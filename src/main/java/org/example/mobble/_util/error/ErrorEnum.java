package org.example.mobble._util.error;

public enum ErrorEnum {
    // 나중에 수정해서 사용함

    /*
     * 302 Found - 해당 정보가 존재함을 알리는 상태 코드
     */
    FOUND(302, "존재하는 정보"),

    /*
     * 302 Found - 해당 유저명은 이미 존재합니다.
     */
    FOUND_USER_TO_USERNAME(302, "해당 유저명은 이미 존재합니다."),

    /*
     * 400 Bad Request - 올바르지 요청을 알리는 상태 코드
     */
    BAD_REQUEST(400, "올바르지 않은 요청"),

    /*
     * 400 Bad Request - 전달된 데이터가 존재하지 않습니다.
     */
    BAD_REQUEST_NO_EXISTS_FILE(400, "파일이 유실되었습니다."),

    /*
     * 400 Bad Request - 전달된 비밀번호가 존재하지 않습니다.
     */
    BAD_REQUEST_NO_EXISTS_PASSWORD(400, "비밀번호가 유실되었습니다."),

    /*
     * 400 Bad Request - 검색어가 존재하지 않습니다.
     */
    BAD_REQUEST_NO_EXISTS_KEYWORD(400, "검색어가 존재하지 않습니다. 접두사를 제외한 1자 이상의 문자를 입력해주세요."),

    /**
     * 400 Bad Request - 접두사 외에 검색어가 존재하지 않습니다.
     */
    BAD_REQUEST_ONLY_PREFIX(400, "접두사만으로는 검색이 어렵습니다."),

    /**
     * 400 Bad Request - 게시물의 id의 값이 null 또는 공백
     */
    BAD_REQUEST_NO_EXISTS_BOARD_ID(400, "게시물이 지정되지 않았습니다."),

    /**
     * 400 Bad Request - 두 개의 board id가 서로 맞지 않음
     */
    BAD_REQUEST_NO_MATCHED_BOARD_ID(400, "수정하고자 하는 게시물과 해당 수정본의 고유 번호가 맞지 않습니다."),

    /*
     * 400 Bad Request - Enum에 존재하지 않는 검색 항목
     */
    BAD_REQUEST_NO_EXISTS_SEARCH_KEY(400, "존재하지 않는 검색 항목입니다."),

    /*
     * 401 Unauthorized - 사용자의 로그인(인증)이 되지 않음을 알리는 상태 코드
     */
    UNAUTHORIZED(401, "인증되지 않은 사용자"),

    /*
     * 401 Unauthorized - 사용자의 로그인(인증)이 되지 않았습니다.
     */
    UNAUTHORIZED_NO_EXISTS_USER_INFO(401, "로그인 후 이용부탁드립니다."),

    /*
     * 403 Forbidden - 권한 없음을 알리는 상태 코드
     */
    FORBIDDEN(403, "해당 권한이 없는 사용자"),

    /*
     * 403 Forbidden - 해당 계정에 대한 권한이 존재하지 않습니다.
     */
    FORBIDDEN_USER_TO_USER(403, "해당 계정에 대한 권한이 존재하지 않습니다."),

    /*
     * 403 Forbidden - 비밀번호가 DB에 저장된 정보와 다릅니다.
     */
    FORBIDDEN_NO_MATCH_PASSWORD(403, "비밀번호가 알맞지 않습니다."),

    /*
     * 403 Forbidden - 권한 갖지 않은 유저의 CRUD 요청
     */
    FORBIDDEN_USER_AT_BOARD(403, "해당 게시글의 권한이 존재하지 않습니다."),

    /*
     * 404 Not Found - 특정 객체를 찾을 수 없음을 알리는 상태 코드
     */
    NOT_FOUND(404, "찾을 수 없음"),

    /*
     * 404 Not Found - 해당 유저명이 존재하지 않습니다.
     */
    NOT_FOUND_USER_TO_USERNAME(404, "해당 유저명이 존재하지 않습니다."),

    /*
     * 404 Not Found - 해당 유저가 존재하지 않습니다.
     */
    NOT_FOUND_USER_TO_USERID(404, "해당 유저가 존재하지 않습니다."),

    /*
     * 404 Not Found - 게시글을 찾을 수 없음
     */
    NOT_FOUND_BOARD(404, "게시글을 찾을 수 없습니다."),

    /*
     * 500 Internal Server Error - 알 수 없는 오류 발생 시 기본 메시지입니다.
     */
    UNKNOWN_SERVER_ERROR(500, "알 수 없는 오류가 발생했습니다. 관리자에게 문의해주세요"),

    /*
     * 500 Internal Server Error - 데이터베이스에 저장된 값이 코드와 일치하지 않을 때 발생합니다.
     * 예: DB의 뱃지 타입 '월간기록'을 Java Enum으로 변환하려 할 때, 해당 Enum 상수가 없는 경우.
     */
    INVALID_DATABASE_DATA(500, "서버 데이터에 문제가 발생했습니다. 관리자에게 문의해주세요.");

    /*
     * HTTP 상태 코드
     */
    private final int status;

    /*
     * 클라이언트에 보여줄 에러 메시지
     */
    private final String message;

    /*
     * 생성자 - 상태 코드와 메시지를 설정합니다.
     *
     * @param status  HTTP 상태 코드
     * @param message 사용자에게 전달할 에러 메시지
     */
    ErrorEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }

    /*
     * HTTP 상태 코드를 반환합니다.
     *
     * @return HTTP 상태 코드 (예: 400, 401, 404 등)
     */
    public int getStatus() {
        return status;
    }

    /*
     * 에러 메시지를 반환합니다.
     *
     * @return 클라이언트에 보여줄 에러 메시지
     */
    public String getMessage() {
        return message;
    }
}
