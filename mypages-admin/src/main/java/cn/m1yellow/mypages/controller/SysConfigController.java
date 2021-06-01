package cn.m1yellow.mypages.controller;


import cn.m1yellow.mypages.common.api.CommonResult;
import cn.m1yellow.mypages.common.api.ResultCode;
import cn.m1yellow.mypages.common.aspect.WebLog;
import cn.m1yellow.mypages.common.constant.GlobalConstant;
import cn.m1yellow.mypages.common.util.RedisUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统配置控制类
 * 实现 ErrorController 接口，用于重写 error 返回信息，统一 restful 接口数据格式
 * <br>
 * 特性	        @ExceptionHandler	                        ErrorController
 * 获取异常	方法参数直接注入了异常类型	                通过 instanceof 自定义 ErrorInfoBuilder 工具类获取
 * 返回类型	若请求的类型为Ajax则返回JSON，否则返回页面	若请求的媒介类型为HTML 则返回页面 ，否则返回JSON
 * 缺点	    无法处理404类异常	                        很强大，可处理全部错误/异常
 */
@RestController
@RequestMapping("/sys")
public class SysConfigController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(SysConfigController.class);


    @Value("${server.error.path}")
    private String errorPath;
    @Autowired
    private RedisUtil redisUtil;


    @Override
    public String getErrorPath() {
        return errorPath;
    }


    @ApiOperation("错误提示信息")
    // @RequestMapping(value = "${server.error.path:/error}"
    @RequestMapping(value = "error", method = {RequestMethod.GET}, produces = "application/json;charset=utf-8")
    public CommonResult<Object> globalErrorHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {

        if (response == null) {
            return CommonResult.failed();
        }

        int statusCode = response.getStatus();
        ResultCode resultCode = ResultCode.getResultCodeByCode(statusCode);
        switch (resultCode) {
            case VALIDATE_FAILED:
                return CommonResult.validateFailed();
            case UNAUTHORIZED:
                return CommonResult.unauthorized();
            case FORBIDDEN:
                return CommonResult.forbidden();
            case NOT_FOUND:
                return CommonResult.notFoundFailed();
            case SUCCESS: // 是直接请求调用 /error，不是错误拦截进来的情况，直接调用也返回错误信息
                //return CommonResult.success();
            default:
                return CommonResult.failed();
        }
    }

    @ApiOperation("全局参数列表")
    @RequestMapping(value = "properties", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    //@Cacheable(value = GlobalConstant.CACHE_10MIN, key = "T(cn.m1yellow.mypages.common.constant.GlobalConstant).SYS_CONFIG_MAP_CACHE_KEY", unless = "#result==null")
    public CommonResult<Map<String, Object>> getProperties() {

        Map<String, Object> properties = new HashMap<>();

        // 排序优先级
        int[] sortValues = GlobalConstant.SORT_VALUES;

        properties.put("sortValues", sortValues);

        return CommonResult.success(properties);
    }

    @ApiOperation("根据KEY删除redis缓存")
    @RequestMapping(value = "delRedisCache", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    public CommonResult<Object> delRedisCache(@RequestParam String cacheKey) {

        if (StringUtils.isBlank(cacheKey)) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        redisUtil.del(cacheKey);

        return CommonResult.success();
    }

}
