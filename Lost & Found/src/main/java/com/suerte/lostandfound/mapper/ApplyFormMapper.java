package com.suerte.lostandfound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suerte.lostandfound.entity.ApplyForm;
import com.suerte.lostandfound.vo.res.UserApplyRes;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: Demon
 * @Date: 2022/5/2
 * @Description:
 */
@Repository
public interface ApplyFormMapper extends BaseMapper<ApplyForm> {


    List<UserApplyRes> getApplyFormByUid(@Param("uid") Integer uid
            ,                            @Param("failed") Integer failed,
                                         @Param("inProgress") Integer inProgress,
                                         @Param("success") Integer success);

}
