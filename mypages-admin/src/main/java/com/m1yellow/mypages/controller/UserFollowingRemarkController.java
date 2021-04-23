package com.m1yellow.mypages.controller;


import com.m1yellow.mypages.common.api.CommonResult;
import com.m1yellow.mypages.entity.UserFollowingRemark;
import com.m1yellow.mypages.service.UserFollowingRemarkService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户关注备注（便签）表 前端控制器
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
@RestController
@RequestMapping("/remark")
public class UserFollowingRemarkController {

    private static final Logger logger = LoggerFactory.getLogger(UserFollowingRemarkController.class);

    @Autowired
    private UserFollowingRemarkService userFollowingRemarkService;


    @ApiOperation("添加/更新用户标签")
    @RequestMapping(value = "add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public CommonResult<UserFollowingRemark> add(@RequestBody UserFollowingRemark remark) {

        if (remark == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        if (!userFollowingRemarkService.saveOrUpdate(remark)) {
            logger.error("添加/更新标签失败");
            return CommonResult.failed("添加/更新标签失败");
        }

        return CommonResult.success(remark);
    }


    @ApiOperation("移除用户标签")
    @RequestMapping(value = "remove", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public CommonResult<String> remove(@RequestParam Long id) {

        if (id == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        if (!userFollowingRemarkService.removeById(id)) {
            logger.error("移除失败，id:" + id);
            return CommonResult.failed("移除失败");
        }

        return CommonResult.success("操作成功");
    }

}
