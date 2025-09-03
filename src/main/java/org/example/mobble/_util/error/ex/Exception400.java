package org.example.mobble._util.error.ex;

import org.example.mobble._util.error.ErrorEnum;

public class Exception400 extends RuntimeException {
    /**
     * 400 Bad Request (잘못된 요청)
     *
     * @param errorEnum
     */
    public Exception400(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
    }

    public Exception400(String message) {
        super(message);
    }
}
