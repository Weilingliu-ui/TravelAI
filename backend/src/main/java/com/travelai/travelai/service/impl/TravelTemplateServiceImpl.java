package com.travelai.travelai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelai.travelai.entity.TravelTemplate;
import com.travelai.travelai.mapper.TravelTemplateMapper;
import com.travelai.travelai.service.TravelTemplateService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class TravelTemplateServiceImpl extends ServiceImpl<TravelTemplateMapper, TravelTemplate> implements TravelTemplateService {

    @Resource
    private TravelTemplateMapper travelTemplateMapper;
}
