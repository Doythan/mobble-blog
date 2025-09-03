package org.example.mobble._util.error.ex;

import org.example.mobble._util.error.ErrorEnum;

public class Exception403 extends RuntimeException {
    /**
     * 403 Forbidden (접근 금지 및 권한 없음)
     *
     * @param errorEnum
     */
    public Exception403(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
    }

    public Exception403(String message) {
        super(message);
    }
}
