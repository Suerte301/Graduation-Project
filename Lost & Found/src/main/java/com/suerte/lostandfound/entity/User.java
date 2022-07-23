package com.suerte.lostandfound.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @Author: Demon
 * @Date: 2022/2/4
 * @Description:
 */
@Data
@TableName("sys_user")
public class User implements UserDetails {
    private static final long serialVersionUID = 7042497814552027949L;
    @TableId(type = IdType.AUTO)
    private Integer id;
    // 用于登录

    private String account;

    private String avatar;

    private String name;
    private String password;
    private String uncrypted;

    @TableField("defaultpass")
    private String defaultpass;
    // 用于找回密码
    private String email;
    private String qq;
    private String tel;
    private String sid;
    @Getter(value = AccessLevel.NONE)
    private Integer enabled;
    @Getter(value = AccessLevel.NONE)
    private Integer locked;
    @TableField(exist = false)
    private List<Role> roles=new ArrayList<>();

    private Date createDate;



    public User() {
        this.enabled=1;
        this.locked=0;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getUsername() {
        return account;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return locked==0;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return enabled==1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }




    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Integer getLocked() {
        return locked;
    }

    public void setLocked(Integer locked) {
        this.locked = locked;
    }

    public List<Role> getRoles() {
        return roles;
    }



    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}