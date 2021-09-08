package cn.m1yellow.mypages.excavation.service;

import cn.m1yellow.mypages.excavation.entity.SysConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 系统配置表 服务类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-06-11
 */
public interface SysConfigService extends IService<SysConfig> {

    Map<String, String> getSysConfigs();

    String getConfigValueByKey(String configKey);

}
