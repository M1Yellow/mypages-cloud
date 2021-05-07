package cn.m1yellow.mypages.security.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Redis 缓存切面，防止 Redis 宕机影响正常业务逻辑
 * Created by macro on 2020/3/17.
 */
@Aspect
@Component
@Order(2)
public class RedisCacheAspect {
    private static final Logger logger = LoggerFactory.getLogger(RedisCacheAspect.class);

    //@Pointcut("execution(public * com.macro.mall.portal.service.*CacheService.*(..)) || execution(public * com.macro.mall.service.*CacheService.*(..))")
    @Pointcut("@annotation(cn.m1yellow.mypages.security.aspect.CacheException)")
    public void cacheAspect() {
    }

    @Around("cacheAspect()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            // 有 CacheException 注解的方法需要抛出异常
            if (method.isAnnotationPresent(CacheException.class)) {
                throw throwable;
            } else {
                logger.error(throwable.getMessage());
            }
        }
        return result;
    }

}
