package cn.m1yellow.mypages.common.aspect;

import cn.m1yellow.mypages.common.api.ResultCode;
import cn.m1yellow.mypages.common.constant.GlobalConstant;
import cn.m1yellow.mypages.common.util.ObjectUtil;
import cn.m1yellow.mypages.common.util.RedisUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * 通过切面拦截，处理一些业务
 */
@Aspect
@Component
//@Profile({"dev", "test"})
public class DoCacheAspect {

    private final static Logger logger = LoggerFactory.getLogger(DoCacheAspect.class);
    /**
     * 换行符
     */
    private static final String LINE_SEPARATOR = System.lineSeparator();
    /**
     * 方法是否出现异常
     */
    private static ThreadLocal<Boolean> localExceptionFlag = new ThreadLocal();


    @Autowired
    private RedisUtil redisUtil;


    /**
     * 以自定义 @WebLog 注解为切点
     */
    @Pointcut("@annotation(cn.m1yellow.mypages.common.aspect.DoCache)")
    public void DoCache() {
    }


    // 切面方法执行顺序，@Around -> @Before -> 业务代码 -> @AfterThrowing -> @After -> @AfterReturning

    @AfterThrowing("DoCache()")
    public void doAfterThrowing(JoinPoint joinPoint) throws Throwable {
        localExceptionFlag.set(true);
    }

    /**
     * 方法执行返回，执行切面任务，注意，如果方法抛异常，是没有返回的，这个方法不会执行。不过正好“曲线救国”，达到出现异常不处理缓存的目的
     *
     * @param joinPoint
     * @param rtv
     */
    @AfterReturning(returning = "rtv", pointcut = "DoCache()")
    public void doAfterReturning(JoinPoint joinPoint, Object rtv) {

        Boolean isException = localExceptionFlag.get();
        localExceptionFlag.remove();
        if (isException != null && isException) {
            logger.info(">>>> 方法出现异常，不进行缓存操作");
            return;
        }

        // 从返回结果中取 code
        Long code = null;
        try {
            Field field = rtv.getClass().getDeclaredField("code");
            if (field != null) {
                // 设置对象的访问权限，保证对 private 的属性的访问
                field.setAccessible(true);
                Object codeObj = field.get(rtv);
                field.setAccessible(false);
                if (codeObj != null) {
                    code = Long.parseLong(ObjectUtil.getString(codeObj));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        if (code != null && !code.equals(ResultCode.SUCCESS.getCode())) {
            // TODO 方法未执行成功，不操作缓存
            logger.info(">>>> 方法未执行成功，不进行缓存操作");
            return;
        }

        // 从请求参数中获取 userId
        Long userId = null;
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        //Class[] parameterTypes = methodSignature.getParameterTypes();
        Object[] args = joinPoint.getArgs();

        if (args == null || args.length < 1) {
            logger.info(">>>> 参数列表为空");
            return;
        }

        // 获取 userId 的下标
        int userIdIndex = ArrayUtils.indexOf(parameterNames, "userId");
        if (userIdIndex < 0) {
            logger.info(">>>> 请求参数 userId 未找到");
            //return;
        } else {
            userId = Long.parseLong(ObjectUtil.getString(args[userIdIndex]));
            if (userId == null) {
                logger.info(">>>> 获取请求参数中的 userId 失败");
                //return;
            }
        }

        if (userId == null || userId < 1) {
            logger.info(">>>> 进一步从请求对象中获取 userId");
            // 从参数封装对象中取 userId
            Object obj = null;
            Method method = null;
            for (int i = 0; i < args.length; i++) {
                try {
                    obj = args[i];
                    if (obj == null) continue;
                    // TODO 由于使用了 lombok，反射获取不到 get 方法
                    //method = obj.getClass().getDeclaredMethod("getUserId", Long.class); // java.lang.NoSuchMethodException
                    //method = obj.getClass().getMethod("getUserId", Long.class); // java.lang.NoSuchMethodException
                    /*
                    if (method != null) {
                        Object returnObj = method.invoke(obj);
                        if (returnObj == null) continue;
                        userId = Long.parseLong(ObjectUtil.getString(returnObj));
                    }
                    */
                    // 直接获取字段值
                    Field field = obj.getClass().getDeclaredField("userId");
                    if (field == null) continue;
                    // 设置对象的访问权限，保证对 private 的属性的访问
                    field.setAccessible(true);
                    Object returnObj = field.get(obj);
                    field.setAccessible(false);
                    if (returnObj == null) continue;
                    userId = Long.parseLong(ObjectUtil.getString(returnObj));
                    if (userId != null && userId > 0) {
                        break;
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    //e.printStackTrace();
                }

            }
            if (userId == null || userId < 1) {
                logger.info(">>>> 获取请求对象中的 userId 失败");
                return;
            }
        }

        // 删除对应用户的首页缓存
        String cacheKey = GlobalConstant.HOME_PLATFORM_LIST_CACHE_KEY + userId;
        redisUtil.del(cacheKey);
        logger.info(">>>> DoCacheAspect 删除首页列表缓存，cache key: {}", cacheKey);
    }

}
