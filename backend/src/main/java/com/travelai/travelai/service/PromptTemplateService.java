package com.travelai.travelai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travelai.travelai.entity.PromptTemplate;

public interface PromptTemplateService extends IService<PromptTemplate> {

    /**
     * 分页查询 + 模糊搜索
     *
     * @param page   分页对象
     * @param name   按 name 模糊查询（可空）
     * @param type   按 templateType 模糊查询（可空）
     * @return 分页结果
     */
    IPage<PromptTemplate> pageQuery(IPage<PromptTemplate> page, String name, String type);
}
