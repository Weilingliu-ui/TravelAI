package com.travelai.travelai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelai.travelai.entity.City;
import com.travelai.travelai.mapper.CityMapper;
import com.travelai.travelai.service.CityService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class CityServiceImpl extends ServiceImpl<CityMapper, City> implements CityService {

    @Resource
    private CityMapper cityMapper;
}
