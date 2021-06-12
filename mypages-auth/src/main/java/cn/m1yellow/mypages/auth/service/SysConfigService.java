package cn.m1yellow.mypages.auth.service;


import cn.m1yellow.mypages.auth.entity.SysConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 系统配置表 服务类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-05-16
 */
public interface SysConfigService extends IService<SysConfig> {

    Map<String, String> getSysConfigs();

}
