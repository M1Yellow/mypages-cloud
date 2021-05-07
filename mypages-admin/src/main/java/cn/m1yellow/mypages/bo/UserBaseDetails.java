package cn.m1yellow.mypages.bo;

import cn.m1yellow.mypages.entity.UserBase;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * 用户信息详情封装对象
 */
public class UserBaseDetails implements UserDetails {

    private UserBase userBase;

    public UserBase getUserBase() {
        return userBase;
    }

    public void setUserBase(UserBase userBase) {
        this.userBase = userBase;
    }

    public UserBaseDetails(UserBase userBase) {
        this.userBase = userBase;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return userBase.getPassword();
    }

    @Override
    public String getUsername() {
        return userBase.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 锁定时间，null-未锁定；当前时间之前-锁定；当前时间之后-待锁定
        final LocalDateTime lockTime = userBase.getLockTime();
        final LocalDateTime currDate = LocalDateTime.now();
        if (lockTime == null) {
            return true;
        } else if (lockTime.isAfter(currDate)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
