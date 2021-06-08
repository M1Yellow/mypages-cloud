package cn.m1yellow.mypages.gateway.exception;

/**
 * jwt 过期异常
 */
public class JwtExpiredException extends RuntimeException {
    public JwtExpiredException(String message) {
        super(message);
    }
}
