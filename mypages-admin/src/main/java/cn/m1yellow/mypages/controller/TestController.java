package cn.m1yellow.mypages.controller;

import cn.m1yellow.mypages.common.api.CommonResult;
import cn.m1yellow.mypages.service.OssService;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制类
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Value("${user.avatar.savedir}")
    private String saveDir;
    @Autowired
    private OssService ossService;


    @ApiOperation("测试文件路径")
    //@PreAuthorize("hasPermission('/testPath', 'admin')")
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


    @ApiOperation("测试OSS服务")
    //@PreAuthorize("hasPermission('/testPath', 'admin')")
    @RequestMapping(value = "testOss", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public CommonResult<String> testOss() {
        File file = new File("E:\\DocHub\\f1b5c4d9d59242e9992892d078bf6cca.jpeg");
        boolean result = ossService.upload("mypages", "images/" + file.getName(), file);
        if (result) {
            boolean delFlag = ossService.delete("mypages", file.getName());
        }
        return null;
    }

}
