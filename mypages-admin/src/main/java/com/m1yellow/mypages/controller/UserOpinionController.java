package com.m1yellow.mypages.controller;


import com.m1yellow.mypages.common.api.CommonResult;
import com.m1yellow.mypages.common.aspect.DoCache;
import com.m1yellow.mypages.common.aspect.WebLog;
import com.m1yellow.mypages.common.util.CheckParamUtil;
import com.m1yellow.mypages.dto.UserOpinionDto;
import com.m1yellow.mypages.entity.UserOpinion;
import com.m1yellow.mypages.service.UserOpinionService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
    @WebLog
    @DoCache
    public CommonResult<UserOpinion> add(UserOpinionDto opinion) {

        CheckParamUtil.validate(opinion);

        UserOpinion saveOpinion = new UserOpinion();
        BeanUtils.copyProperties(opinion, saveOpinion);
        if (!userOpinionService.saveOrUpdate(saveOpinion)) {
            logger.error("添加/更新观点失败");
            return CommonResult.failed("操作失败");
        }

        // 重新加载对象
        UserOpinion reloadOpinion = userOpinionService.getById(saveOpinion.getId());

        return CommonResult.success(reloadOpinion);
    }


    @ApiOperation("移除观点")
    @RequestMapping(value = "remove", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    @DoCache
    public CommonResult<String> remove(@RequestParam Long userId, @RequestParam Long id) {

        if (userId == null || id == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("id", id);
        if (!userOpinionService.removeByMap(params)) {
            logger.error("移除失败，userId: {}, id: {}", userId, id);
            return CommonResult.failed("操作失败");
        }

        return CommonResult.success();
    }

}
