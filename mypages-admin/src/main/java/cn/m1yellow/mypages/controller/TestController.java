package cn.m1yellow.mypages.controller;

import com.alibaba.fastjson.JSON;
import cn.m1yellow.mypages.common.api.CommonResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制类
 */
@RestController
@RequestMapping("/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Value("${user.avatar.savedir}")
    private String saveDir;


    @ApiOperation("测试文件路径")
    @RequestMapping(value = "testPath", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public CommonResult<String> testPath(HttpServletRequest request) {

        Map<String, Object> pathResult = new HashMap<>();
        // 项目 classpath 路径
        String classPath = UserFollowingController.class.getResource("/").getPath() + saveDir;
        pathResult.put("pathResult", classPath);

        // tomcat 服务器路径
        String realPath = request.getSession().getServletContext().getRealPath(saveDir);
        pathResult.put("realPath", realPath);

        String result = JSON.toJSONString(pathResult);
        System.out.println(result);

        return CommonResult.success(result);
    }

}
