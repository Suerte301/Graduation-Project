package com.suerte.lostandfound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suerte.lostandfound.entity.Role;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: Demon
 * @Date: 2022/2/4
 * @Description:
 */
@Repository
public interface RoleMapper extends BaseMapper<Role> {
    List<Role> getUserRolesByUid(@Param("id") Integer id);
}
