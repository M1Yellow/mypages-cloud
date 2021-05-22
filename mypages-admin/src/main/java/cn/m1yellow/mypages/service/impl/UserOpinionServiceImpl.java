package cn.m1yellow.mypages.service.impl;

import cn.m1yellow.mypages.common.constant.GlobalConstant;
import cn.m1yellow.mypages.entity.UserOpinion;
import cn.m1yellow.mypages.mapper.UserOpinionMapper;
import cn.m1yellow.mypages.service.UserOpinionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 用户观点看法表 服务实现类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
@Service
public class UserOpinionServiceImpl extends ServiceImpl<UserOpinionMapper, UserOpinion> implements UserOpinionService {

    @Override
    public int getOpinionCount(Long userId, Long platformId, Long typeId) {
        QueryWrapper<UserOpinion> opinionQueryWrapper = new QueryWrapper<>();
        opinionQueryWrapper.eq("user_id", userId);
        opinionQueryWrapper.eq("platform_id", platformId);
        opinionQueryWrapper.eq("opinion_type", typeId);

        return count(opinionQueryWrapper);
    }

    @Override
    public Page<UserOpinion> getPagingList(Long userId, Long platformId, Long typeId, Integer pageNo, Integer pageSize,
                                           Map<String, Object> params) {

        QueryWrapper<UserOpinion> opinionQueryWrapper = new QueryWrapper<>();
        opinionQueryWrapper.eq("user_id", userId);
        opinionQueryWrapper.eq("platform_id", platformId);
        opinionQueryWrapper.eq("opinion_type", typeId);

        if (pageNo == null) {
            pageNo = GlobalConstant.PAGE_NO_DEFAULT;
        }
        if (pageSize == null) {
            pageSize = GlobalConstant.PAGE_SIZE_DEFAULT;
        }
        // TODO 处理分页数据
        int recordNo = (pageNo - 1) * pageSize;

        Page<UserOpinion> opinionPage = new Page<>(recordNo, pageSize);
        opinionPage = page(opinionPage, opinionQueryWrapper);

        return opinionPage;
    }
}
