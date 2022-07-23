package com.suerte.lostandfound.service;

import com.suerte.lostandfound.vo.req.ApplyFormUpdateReq;
import com.suerte.lostandfound.vo.req.ComplaintUpdateReq;
import org.springframework.data.relational.core.sql.In;

/**
 * @Author: Demon
 * @Date: 2022/5/28
 * @Description:
 */
public interface AdminService {

    void delUser(Integer id) throws Exception;

    void updateForm(ApplyFormUpdateReq applyFormUpdateReq) throws  Exception;
    void updateComplaintForm(ComplaintUpdateReq complaintUpdateReq) throws  Exception;

    void delGoods(String id);

    void delComplaint(String id);
}
