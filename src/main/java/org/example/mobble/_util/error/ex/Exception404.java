package org.example.mobble._util.error.ex;

import org.example.mobble._util.error.ErrorEnum;

public class Exception404 extends RuntimeException {
    /**
     * 404 Not Found (찾을 수 없음)
     *
     * @param errorEnum
     */
    public Exception404(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
    }

    public Exception404(String message) {
        super(message);
    }
}
