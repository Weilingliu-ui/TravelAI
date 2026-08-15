package com.travelai.travelai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travelai.travelai.entity.Attraction;
import com.travelai.travelai.vo.AiAttractionCandidate;

import java.util.List;

public interface AttractionService extends IService<Attraction> {

    IPage<Attraction> pageQuery(Integer pageNum, Integer pageSize, Long cityId, String keyword, String tag, String sortBy);

    List<Attraction> listHotAttractions(Integer limit);

    List<Attraction> searchByName(String keyword);

    Attraction getDetail(Long id);

    /** 根据城市和标签查询景点 */
    List<Attraction> listByCityAndTags(Long cityId, List<String> tags);

    /** 为 AI 推荐候选景点（根据目的地+兴趣，按评分热度排序，返回指定数量） */
    List<AiAttractionCandidate> recommendForAi(String destination, String interests, Integer limit);
}
