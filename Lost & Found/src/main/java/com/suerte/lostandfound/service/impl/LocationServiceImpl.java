package com.suerte.lostandfound.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suerte.lostandfound.entity.Location;
import com.suerte.lostandfound.mapper.LocationMapper;
import com.suerte.lostandfound.service.LocationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: Demon
 * @Date: 2022/4/20
 * @Description:
 */
@Service
@Transactional
public class LocationServiceImpl extends ServiceImpl<LocationMapper, Location> implements LocationService {
}
