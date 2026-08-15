package com.travelai.travelai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelai.travelai.entity.TravelDay;
import com.travelai.travelai.mapper.TravelDayMapper;
import com.travelai.travelai.service.TravelDayService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class TravelDayServiceImpl extends ServiceImpl<TravelDayMapper, TravelDay> implements TravelDayService {

    @Resource
    private TravelDayMapper travelDayMapper;
}
