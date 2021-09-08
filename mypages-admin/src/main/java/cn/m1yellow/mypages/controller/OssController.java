package cn.m1yellow.mypages.controller;

import cn.m1yellow.mypages.common.api.CommonResult;
import cn.m1yellow.mypages.common.dto.OssCallbackResult;
import cn.m1yellow.mypages.common.dto.OssPolicyResult;
import cn.m1yellow.mypages.common.service.OssService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Oss对象存储管理Controller
 * Created by macro on 2018/4/26.
 */
@RestController
@RequestMapping("/aliyun/oss")
public class OssController {

    @Autowired
    private OssService ossService;

    @ApiOperation(value = "Oss上传签名生成")
    @RequestMapping(value = "/policy", method = RequestMethod.GET)
    public CommonResult<OssPolicyResult> policy() {
        OssPolicyResult result = ossService.policy();
        return CommonResult.success(result);
    }

    @ApiOperation(value = "Oss上传成功回调")
    @RequestMapping(value = "callback", method = RequestMethod.POST)
    public CommonResult<OssCallbackResult> callback(HttpServletRequest request) {
        OssCallbackResult ossCallbackResult = ossService.callback(request);
        return CommonResult.success(ossCallbackResult);
    }

}
