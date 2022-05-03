package com.suerte.lostandfound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suerte.lostandfound.entity.Category;
import com.suerte.lostandfound.vo.res.CategoryRes;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: Demon
 * @Date: 2022/4/20
 * @Description:
 */
@Repository
public interface CategoryMapper extends BaseMapper<Category> {

    List<CategoryRes> getGoodsInfo(@Param("status") Integer status,Integer uid);

    int getMaxType();

}
