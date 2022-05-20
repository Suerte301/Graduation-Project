package com.suerte.lostandfound.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suerte.lostandfound.entity.ComplaintForm;
import com.suerte.lostandfound.mapper.ComplaintFormMapper;
import com.suerte.lostandfound.service.ComplaintFormService;
import org.springframework.stereotype.Service;

/**
 * @Author: Demon
 * @Date: 2022/5/18
 * @Description:
 */
@Service
public class ComplaintFormServiceImpl extends ServiceImpl<ComplaintFormMapper, ComplaintForm> implements ComplaintFormService {
}
