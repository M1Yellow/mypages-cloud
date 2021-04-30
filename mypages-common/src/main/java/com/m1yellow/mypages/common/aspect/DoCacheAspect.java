package com.m1yellow.mypages.common.aspect;

import com.m1yellow.mypages.common.constant.GlobalConstant;
import com.m1yellow.mypages.common.util.ObjectUtil;
import com.m1yellow.mypages.common.util.RedisUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 通过切面拦截，处理一些业务
 */
@Aspect
@Component
//@Profile({"dev", "test"})
public class DoCacheAspect {

    private final static Logger logger = LoggerFactory.getLogger(DoCacheAspect.class);

    @Autowired
    private RedisUtil redisUtil;


    /**
     * 换行符
     */
    private static final String LINE_SEPARATOR = System.lineSeparator();


    /**
     * 以自定义 @WebLog 注解为切点
     */
    @Pointcut("@annotation(com.m1yellow.mypages.common.aspect.DoCache)")
    public void DoCache() {
    }


    // 切面方法执行顺序，@Around -> @Before -> 业务代码 -> @After -> @AfterThrowing -> @AfterReturning

    /**
     * 在切点之后织入
     *
     * @throws Throwable
     */
    @After("DoCache()")
    public void doAfter(JoinPoint joinPoint) throws Throwable {

        // 从请求参数中获取 userId
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        // 获取 userId 的下标
        int userIdIndex = ArrayUtils.indexOf(parameterNames, "userId");
        if (userIdIndex < 0) {
            logger.info(">>>> 请求参数 userId 未找到");
            return;
        }

        Object[] args = joinPoint.getArgs();
        Long userId = Long.parseLong(ObjectUtil.getString(args[userIdIndex]));
        if (userId == null) {
            logger.info(">>>> 请求参数 userId 获取失败");
            return;
        }

        // 删除对应用户的首页缓存
        String cacheKey = GlobalConstant.HOME_PLATFORM_LIST_CACHE_KEY + userId;
        redisUtil.del(cacheKey);
        logger.info(">>>> 删除首页列表缓存，cache key: {}", cacheKey);
    }

}
