package org.example.mobble._util.error.ex;

import org.example.mobble._util.error.ErrorEnum;

public class Exception302 extends RuntimeException {
    /**
     * 302 FOUND (존재하는 정보)
     *
     * @param errorEnum
     */
    public Exception302(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
    }

    public Exception302(String message) {
        super(message);
    }
}
