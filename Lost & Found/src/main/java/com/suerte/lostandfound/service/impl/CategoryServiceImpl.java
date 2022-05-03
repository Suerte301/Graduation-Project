package com.suerte.lostandfound.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suerte.lostandfound.entity.Category;
import com.suerte.lostandfound.eum.GoodsStatusEnum;
import com.suerte.lostandfound.mapper.CategoryMapper;
import com.suerte.lostandfound.service.CategoryService;
import com.suerte.lostandfound.vo.res.CategoryRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @Author: Demon
 * @Date: 2022/4/20
 * @Description:
 */
@Service
@Transactional
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<CategoryRes> getGoodsInfo(Integer status,Integer uid){
        return categoryMapper.getGoodsInfo(Optional.ofNullable(status).orElse(GoodsStatusEnum.REVIEW_PASSED.getType()),uid);
    }

}
