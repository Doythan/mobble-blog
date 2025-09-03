package org.example.mobble._util.error;

public enum ErrorEnum {
    // 나중에 수정해서 사용함

    /*
     * 302 Found - 해당 정보가 존재함을 알리는 상태 코드
     */
    FOUND(302, "존재하는 정보"),

    /**
     * 400 Bad Request - 올바르지 요청을 알리는 상태 코드
     */
    BAD_REQUEST(400, "올바르지 않은 요청"),

    /**
     * 401 Unauthorized - 사용자의 로그인(인증)이 되지 않음을 알리는 상태 코드
     */
    UNAUTHORIZED(401, "인증되지 않은 사용자"),

    /**
     * 403 Forbidden - 권한 없음을 알리는 상태 코드
     */
    FORBIDDEN(403, "해당 권한이 없는 사용자"),

    /**
     * 404 Not Found - 특정 객체를 찾을 수 없음을 알리는 상태 코드
     */
    NOT_FOUND(404, "찾을 수 없음"),

    /**
     * 500 Internal Server Error - 알 수 없는 오류 발생 시 기본 메시지입니다.
     */
    UNKNOWN_SERVER_ERROR(500, "알 수 없는 오류가 발생했습니다. 관리자에게 문의해주세요"),

    /**
     * 500 Internal Server Error - 데이터베이스에 저장된 값이 코드와 일치하지 않을 때 발생합니다.
     * 예: DB의 뱃지 타입 '월간기록'을 Java Enum으로 변환하려 할 때, 해당 Enum 상수가 없는 경우.
     */
    INVALID_DATABASE_DATA(500, "서버 데이터에 문제가 발생했습니다. 관리자에게 문의해주세요.");

    /**
     * HTTP 상태 코드
     */
    private final int status;

    /**
     * 클라이언트에 보여줄 에러 메시지
     */
    private final String message;

    /**
     * 생성자 - 상태 코드와 메시지를 설정합니다.
     *
     * @param status  HTTP 상태 코드
     * @param message 사용자에게 전달할 에러 메시지
     */
    ErrorEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * HTTP 상태 코드를 반환합니다.
     *
     * @return HTTP 상태 코드 (예: 400, 401, 404 등)
     */
    public int getStatus() {
        return status;
    }

    /**
     * 에러 메시지를 반환합니다.
     *
     * @return 클라이언트에 보여줄 에러 메시지
     */
    public String getMessage() {
        return message;
    }
}
