package cn.m1yellow.mypages.service.impl;

import cn.m1yellow.mypages.common.constant.GlobalConstant;
import cn.m1yellow.mypages.common.util.FastJsonUtil;
import cn.m1yellow.mypages.common.util.ObjectUtil;
import cn.m1yellow.mypages.common.util.RedisUtil;
import cn.m1yellow.mypages.entity.SysConfig;
import cn.m1yellow.mypages.mapper.SysConfigMapper;
import cn.m1yellow.mypages.service.SysConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统配置表 服务实现类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-05-16
 */
@Service("sysConfigService")
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    private static final Logger logger = LoggerFactory.getLogger(SysConfigServiceImpl.class);


    @Resource // 非直接依赖模块，@Autowired 注入报错
    private RedisUtil redisUtil;


    @Override
    public Map<String, Object> getSysConfigs() {

        // 先从缓存取
        String configCacheStr = ObjectUtil.getString(redisUtil.get(GlobalConstant.SYS_CONFIG_MAP_CACHE_KEY));
        if (StringUtils.isNotBlank(configCacheStr)) {
            Map<String, Object> sysConfigMap = FastJsonUtil.json2Bean(configCacheStr, Map.class);
            if (!CollectionUtils.isEmpty(sysConfigMap)) {
                return sysConfigMap;
            }
        }

        List<SysConfig> sysConfigList = list();
        if (CollectionUtils.isEmpty(sysConfigList)) {
            return null;
        }
        Map<String, Object> sysConfigMap = sysConfigList.stream().collect(Collectors.toMap(SysConfig::getConfigKey, SysConfig::getConfigValue));
        // 添加缓存，不设置过期时间，有改动的地方，清理缓存之后，会自动重新加载
        if (sysConfigMap != null && sysConfigMap.size() > 0) {
            redisUtil.set(GlobalConstant.SYS_CONFIG_MAP_CACHE_KEY, FastJsonUtil.bean2Json(sysConfigMap));
        }
        return sysConfigMap;
    }

}