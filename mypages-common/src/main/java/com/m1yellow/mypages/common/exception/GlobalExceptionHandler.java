package com.m1yellow.mypages.common.exception;

import com.m1yellow.mypages.common.api.CommonResult;
import com.m1yellow.mypages.common.api.ResultCode;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理
 * Created by macro on 2020/2/27.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 统一处理参数校验错误异常（controller 请求接口参数数据绑定异常）
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(BindException.class)
    public CommonResult processValidException(BindException e) {
        return CommonResult.failed(ResultCode.VALIDATE_FAILED, e.getMessage());
    }

    /**
     * 统一处理参数校验错误异常
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    public CommonResult processValidException(IllegalArgumentException e) {
        return CommonResult.failed(ResultCode.VALIDATE_FAILED, e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = ApiException.class)
    public CommonResult handle(ApiException e) {
        if (e.getErrorCode() != null) {
            return CommonResult.failed(e.getErrorCode());
        }
        return CommonResult.failed(e.getMessage());
    }

    /**
     * 自定义文件保存异常，文件保存失败，抛出此异常，可以让方法中的数据库操作回滚
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = FileSaveException.class)
    public CommonResult handle(FileSaveException e) {
        if (e.getErrorCode() != null) {
            return CommonResult.failed(e.getErrorCode());
        }
        return CommonResult.failed(e.getMessage());
    }

    /**
     * 自定义方法原子性操作异常，整个方法中途操作失败，可手动抛出此异常，中断操作，保证原子性
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = AtomicityException.class)
    public CommonResult handle(AtomicityException e) {
        if (e.getErrorCode() != null) {
            return CommonResult.failed(e.getErrorCode());
        }
        return CommonResult.failed(e.getMessage());
    }

}
