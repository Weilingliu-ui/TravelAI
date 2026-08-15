package com.travelai.travelai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelai.travelai.entity.PromptTemplate;
import com.travelai.travelai.mapper.PromptTemplateMapper;
import com.travelai.travelai.service.PromptTemplateService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PromptTemplateServiceImpl extends ServiceImpl<PromptTemplateMapper, PromptTemplate> implements PromptTemplateService {

    @Resource
    private PromptTemplateMapper promptTemplateMapper;

    @Override
    public IPage<PromptTemplate> pageQuery(IPage<PromptTemplate> page, String name, String type) {
        LambdaQueryWrapper<PromptTemplate> wrapper = new LambdaQueryWrapper<PromptTemplate>()
                .like(StringUtils.hasText(name), PromptTemplate::getName, name)
                .like(StringUtils.hasText(type), PromptTemplate::getTemplateType, type)
                .orderByDesc(PromptTemplate::getCreatedAt);
        return page(page, wrapper);
    }
}
