package cn.m1yellow.mypages.common.exception;

import cn.m1yellow.mypages.common.api.IErrorCode;

/**
 * 自定义不允许访问异常
 */
public class AccessNotAllowException extends RuntimeException {

    private IErrorCode errorCode;

    public AccessNotAllowException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AccessNotAllowException(String message) {
        super(message);
    }

    public AccessNotAllowException(Throwable cause) {
        super(cause);
    }

    public AccessNotAllowException(String message, Throwable cause) {
        super(message, cause);
    }

    public IErrorCode getErrorCode() {
        return errorCode;
    }

}
