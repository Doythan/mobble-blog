package org.example.mobble._util.error.ex;

import org.example.mobble._util.error.ErrorEnum;

public class Exception500 extends RuntimeException {
    /**
     * 500 Internal Server Error (서버 문제)
     *
     * @param errorEnum
     */
    public Exception500(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
    }

    public Exception500(String message) {
        super(message);
    }
}
