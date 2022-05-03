package com.suerte.lostandfound.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suerte.lostandfound.entity.Category;
import com.suerte.lostandfound.vo.res.CategoryRes;

import java.util.List;

/**
 * @Author: Demon
 * @Date: 2022/4/23
 * @Description:
 */
public interface CategoryService extends IService<Category> {
    List<CategoryRes> getGoodsInfo(Integer status, Integer uid);
}
