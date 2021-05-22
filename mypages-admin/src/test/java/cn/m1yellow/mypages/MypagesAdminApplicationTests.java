package cn.m1yellow.mypages;

import cn.m1yellow.mypages.common.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
class MypagesAdminApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(MypagesAdminApplicationTests.class);

    @Autowired
    private DataSource dataSource;
    @Resource
    private RedisUtil redisUtil;


    @Test
    public void showDatasource() throws SQLException {
        Connection data = dataSource.getConnection();
        System.out.println("------" + data.getClass());
        System.out.println("------" + dataSource.getClass());
        data.close();
    }

    @Test
    public void testLogging() {
        //<!-- 日志级别：TRACE < DEBUG < INFO < WARN < ERROR [< FATAL（致命）] -->
        logger.trace("====测试日志级别TRACE====");
        logger.debug("====测试日志级别DEBUG====");
        logger.info("====测试日志级别INFO====");
        logger.warn("====测试日志级别WARN====");
        logger.error("====测试日志级别ERROR====");

    }

    @Test
    public void testRedis() {
        // redisTemplate 操作不同的数据类型，api和我们的指令是一样的
        // opsForValue 操作字符串 类似String
        // opsForList 操作List 类似List
        // opsForSet
        // opsForHash
        // opsForZSet
        // opsForGeo
        // opsForHyperLogLog
        // 除了进本的操作，我们常用的方法都可以直接通过redisTemplate操作，比如事务，和基本的 CRUD
        // 获取redis的连接对象
        // RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        // connection.flushDb();
        // connection.flushAll();
        //redisUtil.set("mykey", "测试 redis 序列化是否乱码1111");
        //logger.info(redisUtil.get("mykey").toString());

        System.out.println(redisUtil.hkeys("page_1_3_9"));
        redisUtil.hdelall("page_1_3_9");

    }

}
