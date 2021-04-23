package com.m1yellow.mypages.controller;


import com.m1yellow.mypages.common.api.CommonResult;
import com.m1yellow.mypages.entity.UserOpinion;
import com.m1yellow.mypages.service.UserOpinionService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户观点看法表 前端控制器
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
@RestController
@RequestMapping("/opinion")
public class UserOpinionController {

    private static final Logger logger = LoggerFactory.getLogger(UserOpinionController.class);

    @Autowired
    private UserOpinionService userOpinionService;


    @ApiOperation("添加/更新观点")
    @RequestMapping(value = "add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public CommonResult<UserOpinion> add(UserOpinion opinion) {

        if (opinion == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        if (!userOpinionService.saveOrUpdate(opinion)) {
            logger.error("添加/更新观点失败");
            return CommonResult.failed("添加/更新观点失败");
        }

        return CommonResult.success(opinion);
    }


    @ApiOperation("移除观点")
    @RequestMapping(value = "remove", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public CommonResult<String> remove(@RequestParam Long id) {

        if (id == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        if (!userOpinionService.removeById(id)) {
            logger.error("移除失败，id:" + id);
            return CommonResult.failed("移除失败");
        }

        return CommonResult.success("操作成功");
    }

}
