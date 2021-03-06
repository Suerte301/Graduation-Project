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


    public List<User> getNormalUser(){
        return userMapper.getNormalUser();
    }

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

        // ????????????????????????name
        List<User> list = list(new LambdaQueryWrapper<User>().eq(ObjectUtils.isNotEmpty(user.getUsername()), User::getUsername, user.getUsername()));
        if (CollectionUtils.isNotEmpty(list)){
            throw new CustomException("????????????,???????????????");
        }


        // ????????????????????????????????????
        User one = getOne(new LambdaQueryWrapper<User>().eq(ObjectUtils.isNotEmpty(user.getEmail()), User::getEmail, user.getEmail()));
        if (ObjectUtils.isNotEmpty(one)){
            throw new CustomException("???????????????????????????,???????????????");
        }

        // ??????
        int flag = userMapper.addUser(user);

        if (flag==0){
            throw new CustomException("????????????,???????????????");
        }

        UserRole userRole = new UserRole();
        userRole.setUid(user.getId());


        // ????????????????????????????????????
        Role role = roleMapper.selectOne(new LambdaQueryWrapper<Role>().like(Role::getName, "user"));
        // ????????????
        if (ObjectUtils.isEmpty(role)){
            throw new CustomException("??????????????????,??????????????????");
        }
        userRole.setRid(role.getId());
        int insert = userRoleMapper.authorizedUser(userRole);
        if (insert==0){
            throw new CustomException("????????????");
        }

        return HttpResult.ok(true,"????????????");
    }
}
