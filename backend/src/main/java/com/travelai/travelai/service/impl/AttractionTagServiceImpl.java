package com.travelai.travelai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelai.travelai.entity.AttractionTag;
import com.travelai.travelai.mapper.AttractionTagMapper;
import com.travelai.travelai.service.AttractionTagService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class AttractionTagServiceImpl extends ServiceImpl<AttractionTagMapper, AttractionTag> implements AttractionTagService {

    @Resource
    private AttractionTagMapper attractionTagMapper;
}
