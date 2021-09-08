package cn.m1yellow.mypages;

import cn.m1yellow.mypages.common.service.OssService;
import cn.m1yellow.mypages.common.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@SpringBootTest
class MypagesAdminApplicationTests {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private OssService ossService;

    @Value("${aliyun.oss.dir.avatar}")
    private String ALIYUN_OSS_DIR_AVATAR;


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
        log.trace("====测试日志级别TRACE====");
        log.debug("====测试日志级别DEBUG====");
        log.info("====测试日志级别INFO====");
        log.warn("====测试日志级别WARN====");
        log.error("====测试日志级别ERROR====");

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
        //log.info(redisUtil.get("mykey").toString());

        System.out.println(redisUtil.hkeys("page_1_3_9"));
        redisUtil.hdelall("page_1_3_9");

    }

    @Test
    public void testOSS() throws Exception {
        File file = new File("E:\\DocHub\\f1b5c4d9d59242e9992892d078bf6cca.jpeg");
        InputStream is = new FileInputStream(file);
        String path = ossService.getPathV2(ALIYUN_OSS_DIR_AVATAR, file.getName());
        String url = ossService.upload("mypages", is, path, false);
        if (is != null) {
            is.close();
        }
        System.out.println(url);
    }

    @Test
    public void testOSSDel() throws Exception {
        ossService.delete("test1", "images/20210713");
        ossService.delete("test1", "images");
    }

}
