package com.suerte.lostandfound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suerte.lostandfound.entity.Goods;
import com.suerte.lostandfound.eum.GoodsStatusEnum;
import com.suerte.lostandfound.vo.res.UserGoodsRes;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: Demon
 * @Date: 2022/4/20
 * @Description:
 */
@Repository
public interface GoodsMapper extends BaseMapper<Goods> {

    List<UserGoodsRes> getGoodsByUid(@Param("uid")Integer uid,
                                     @Param("failed")Integer failed,
                                     @Param("inProgress")Integer inProgress,
                                     @Param("success")Integer success);
}
