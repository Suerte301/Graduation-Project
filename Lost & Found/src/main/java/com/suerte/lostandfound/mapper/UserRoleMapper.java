package com.suerte.lostandfound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suerte.lostandfound.entity.UserRole;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Author: Demon
 * @Date: 2022/2/4
 * @Description:
 */
@Repository
public interface UserRoleMapper extends BaseMapper<UserRole> {

    int authorizedUser(@Param("userRole") UserRole userRole);

}
