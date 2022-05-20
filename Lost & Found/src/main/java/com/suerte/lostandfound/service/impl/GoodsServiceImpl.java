package com.suerte.lostandfound.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suerte.lostandfound.entity.Goods;
import com.suerte.lostandfound.eum.GoodsStatusEnum;
import com.suerte.lostandfound.mapper.GoodsMapper;
import com.suerte.lostandfound.service.GoodsService;
import com.suerte.lostandfound.vo.res.UserGoodsRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: Demon
 * @Date: 2022/4/20
 * @Description:
 */
@Service
@Transactional
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<UserGoodsRes> getGoodsByUid(Integer id) {
        return goodsMapper.getGoodsByUid(id,
                GoodsStatusEnum.REVIEW_FAILED.getType(),
                GoodsStatusEnum.REVIEWING.getType(),
                GoodsStatusEnum.REVIEW_PASSED.getType()
                );
    }
}
