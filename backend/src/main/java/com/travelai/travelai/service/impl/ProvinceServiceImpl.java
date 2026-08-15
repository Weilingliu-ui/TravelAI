package com.travelai.travelai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelai.travelai.entity.Province;
import com.travelai.travelai.mapper.ProvinceMapper;
import com.travelai.travelai.service.ProvinceService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class ProvinceServiceImpl extends ServiceImpl<ProvinceMapper, Province> implements ProvinceService {

    @Resource
    private ProvinceMapper provinceMapper;
}
