package com.travelai.travelai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelai.travelai.entity.ScenicTag;
import com.travelai.travelai.mapper.ScenicTagMapper;
import com.travelai.travelai.service.ScenicTagService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class ScenicTagServiceImpl extends ServiceImpl<ScenicTagMapper, ScenicTag> implements ScenicTagService {

    @Resource
    private ScenicTagMapper scenicTagMapper;
}
