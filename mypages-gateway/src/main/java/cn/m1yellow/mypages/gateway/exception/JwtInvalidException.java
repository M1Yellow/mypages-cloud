package cn.m1yellow.mypages.gateway.exception;

/**
 * jwt 失效异常
 */
public class JwtInvalidException extends RuntimeException{
    public JwtInvalidException(String message) {
        super(message);
    }
}
