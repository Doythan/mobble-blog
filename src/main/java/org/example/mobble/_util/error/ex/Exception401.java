package org.example.mobble._util.error.ex;

import org.example.mobble._util.error.ErrorEnum;

public class Exception401 extends RuntimeException {
    /**
     * 401 Unauthorized (인증되지 않음)
     *
     * @param errorEnum
     */
    public Exception401(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
    }

    public Exception401(String message) {
        super(message);
    }
}
