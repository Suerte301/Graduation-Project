package com.suerte.lostandfound.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suerte.lostandfound.constant.RedisConstant;
import com.suerte.lostandfound.entity.Role;
import com.suerte.lostandfound.entity.User;
import com.suerte.lostandfound.entity.UserRole;
import com.suerte.lostandfound.exception.CustomException;
import com.suerte.lostandfound.mapper.RoleMapper;
import com.suerte.lostandfound.mapper.UserMapper;
import com.suerte.lostandfound.mapper.UserRoleMapper;
import com.suerte.lostandfound.vo.HttpResult;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: Demon
 * @Date: 2022/2/4
 * @Description:
 */
@Service
public class UserService extends ServiceImpl<UserMapper,User> implements UserDetailsService {


    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RedissonClient redissonClient;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = getOne(new LambdaQueryWrapper<User>().eq(ObjectUtils.isNotEmpty(username), User::getAccount, username));

        if (ObjectUtils.isNotEmpty(user)) {
            List<Role> roles = roleMapper.getUserRolesByUid(user.getId());
            user.setRoles(roles);
            redissonClient.getBucket(RedisConstant.USER_LOGIN_PREFIX+user.getId()).set(user.getId());
        }
        return user;
    }

    @Transactional(rollbackFor = {Exception.class,CustomException.class})
    public HttpResult signUp(User user) throws CustomException {

        // 判断是否有重复的name
        List<User> list = list(new LambdaQueryWrapper<User>().eq(ObjectUtils.isNotEmpty(user.getUsername()), User::getUsername, user.getUsername()));
        if (CollectionUtils.isNotEmpty(list)){
            throw new CustomException("名字重复,请换个名字");
        }


        // 判断是否存在已绑定的邮箱
        User one = getOne(new LambdaQueryWrapper<User>().eq(ObjectUtils.isNotEmpty(user.getEmail()), User::getEmail, user.getEmail()));
        if (ObjectUtils.isNotEmpty(one)){
            throw new CustomException("此邮箱已经注册过了,请重新登录");
        }

        // 注册
        int flag = userMapper.addUser(user);

        if (flag==0){
            throw new CustomException("注册失败,请稍后再试");
        }

        UserRole userRole = new UserRole();
        userRole.setUid(user.getId());


        // 获取数据库的普通用户角色
        Role role = roleMapper.selectOne(new LambdaQueryWrapper<Role>().like(Role::getName, "user"));
        // 分配权限
        if (ObjectUtils.isEmpty(role)){
            throw new CustomException("无可分配权限,请联系管理员");
        }
        userRole.setRid(role.getId());
        int insert = userRoleMapper.authorizedUser(userRole);
        if (insert==0){
            throw new CustomException("授权失败");
        }

        return HttpResult.ok(true,"注册成功");
    }
}
