package com.m1yellow.mypages.common.aspect;

import com.m1yellow.mypages.common.constant.GlobalConstant;
import com.m1yellow.mypages.common.util.RedisUtil;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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
public class DoSomethingsAspect {

    private final static Logger logger = LoggerFactory.getLogger(DoSomethingsAspect.class);

    @Autowired
    private RedisUtil redisUtil;


    /**
     * 换行符
     */
    private static final String LINE_SEPARATOR = System.lineSeparator();


    /**
     * 以自定义 @WebLog 注解为切点
     */
    @Pointcut("@annotation(com.m1yellow.mypages.common.aspect.DoSomethings)")
    public void DoSomethings() {
    }


    // 切面方法执行顺序，@Around -> @Before -> 业务代码 -> @After -> @AfterThrowing -> @AfterReturning

    /**
     * 在切点之后织入
     *
     * @throws Throwable
     */
    @After("DoSomethings()")
    public void doAfter() throws Throwable {
        // 删除首页缓存
        redisUtil.del(GlobalConstant.HOME_PLATFORM_LIST_CACHE_KEY);
        logger.info(">>>> 删除首页列表缓存，cache key: {}", GlobalConstant.HOME_PLATFORM_LIST_CACHE_KEY);
    }

}
