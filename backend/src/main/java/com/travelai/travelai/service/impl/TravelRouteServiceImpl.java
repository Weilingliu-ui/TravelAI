package com.travelai.travelai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelai.travelai.entity.TravelRoute;
import com.travelai.travelai.mapper.TravelRouteMapper;
import com.travelai.travelai.service.TravelRouteService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class TravelRouteServiceImpl extends ServiceImpl<TravelRouteMapper, TravelRoute> implements TravelRouteService {

    @Resource
    private TravelRouteMapper travelRouteMapper;
}
