package com.suerte.lostandfound.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suerte.lostandfound.entity.Goods;
import com.suerte.lostandfound.vo.res.UserGoodsRes;

import java.util.List;

/**
 * @Author: Demon
 * @Date: 2022/4/23
 * @Description:
 */
public interface GoodsService extends IService<Goods> {
    List<UserGoodsRes> getGoodsByUid(Integer id);
}
