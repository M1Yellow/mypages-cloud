package cn.m1yellow.mypages.service;

import cn.m1yellow.mypages.entity.UserBase;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
public interface UserBaseService extends IService<UserBase> {

    UserBase getByUserName(String userName);

    UserDetails loadUserByUsername(String userName);

    String login(String userName, String password);

}
