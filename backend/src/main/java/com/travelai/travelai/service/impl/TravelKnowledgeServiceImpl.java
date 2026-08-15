package com.travelai.travelai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelai.travelai.entity.TravelKnowledge;
import com.travelai.travelai.mapper.TravelKnowledgeMapper;
import com.travelai.travelai.service.TravelKnowledgeService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class TravelKnowledgeServiceImpl extends ServiceImpl<TravelKnowledgeMapper, TravelKnowledge> implements TravelKnowledgeService {

    @Resource
    private TravelKnowledgeMapper travelKnowledgeMapper;
}
