package com.travelai.travelai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelai.travelai.entity.Attraction;
import com.travelai.travelai.entity.City;
import com.travelai.travelai.mapper.AttractionMapper;
import com.travelai.travelai.mapper.CityMapper;
import com.travelai.travelai.service.AttractionService;
import com.travelai.travelai.vo.AiAttractionCandidate;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttractionServiceImpl extends ServiceImpl<AttractionMapper, Attraction> implements AttractionService {

    @Resource
    private AttractionMapper attractionMapper;

    @Resource
    private CityMapper cityMapper;

    @Override
    public IPage<Attraction> pageQuery(Integer pageNum, Integer pageSize, Long cityId, String keyword, String tag, String sortBy) {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<Attraction>()
                .eq(cityId != null, Attraction::getCityId, cityId)
                .and(StringUtils.hasText(keyword), w -> w
                        .like(Attraction::getName, keyword)
                        .or()
                        .like(Attraction::getDescription, keyword))
                .like(StringUtils.hasText(tag), Attraction::getCategory, tag)
                .eq(Attraction::getStatus, 1);

        if ("rating".equals(sortBy)) {
            wrapper.orderByDesc(Attraction::getRating);
        } else if ("latest".equals(sortBy)) {
            wrapper.orderByDesc(Attraction::getCreatedAt);
        } else {
            wrapper.orderByDesc(Attraction::getVisitCount);
        }

        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<Attraction> listHotAttractions(Integer limit) {
        return list(new LambdaQueryWrapper<Attraction>()
                .eq(Attraction::getStatus, 1)
                .orderByDesc(Attraction::getVisitCount)
                .last("LIMIT " + limit));
    }

    @Override
    public List<Attraction> searchByName(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return List.of();
        }
        return list(new LambdaQueryWrapper<Attraction>()
                .like(Attraction::getName, keyword)
                .eq(Attraction::getStatus, 1)
                .orderByDesc(Attraction::getVisitCount)
                .last("LIMIT 10"));
    }

    @Override
    public Attraction getDetail(Long id) {
        return getById(id);
    }

    @Override
    public List<Attraction> listByCityAndTags(Long cityId, List<String> tags) {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<Attraction>()
                .eq(cityId != null, Attraction::getCityId, cityId)
                .eq(Attraction::getStatus, 1)
                .orderByDesc(Attraction::getVisitCount);

        if (tags != null && !tags.isEmpty()) {
            wrapper.and(w -> {
                for (int i = 0; i < tags.size(); i++) {
                    if (i == 0) {
                        w.like(Attraction::getCategory, tags.get(i));
                    } else {
                        w.or().like(Attraction::getCategory, tags.get(i));
                    }
                }
            });
        }

        return list(wrapper);
    }

    @Override
    public List<AiAttractionCandidate> recommendForAi(String destination, String interests, Integer limit) {
        // 1. 根据目的地名称模糊查找城市
        City city = cityMapper.selectOne(new LambdaQueryWrapper<City>()
                .like(City::getName, destination)
                .last("LIMIT 1"));
        Long cityId = city != null ? city.getId() : null;
        String cityName = city != null ? city.getName() : destination;

        // 2. 提取兴趣标签
        List<String> tags = parseInterestTags(interests);

        // 3. 查景点
        List<Attraction> attractions = listByCityAndTags(cityId, tags);

        // 4. 若结果太少，补充热门景点
        if (attractions.size() < (limit != null ? limit : 20)) {
            attractions = list(new LambdaQueryWrapper<Attraction>()
                    .eq(cityId != null, Attraction::getCityId, cityId)
                    .eq(Attraction::getStatus, 1)
                    .orderByDesc(Attraction::getVisitCount)
                    .last("LIMIT " + (limit != null ? limit : 20)));
        }

        // 5. 截取并转为 DTO
        int size = limit != null ? Math.min(limit, attractions.size()) : Math.min(20, attractions.size());
        return attractions.subList(0, size).stream()
                .map(a -> new AiAttractionCandidate(
                        a.getId(),
                        a.getName(),
                        cityName,
                        a.getCategory(),
                        a.getRating(),
                        a.getVisitCount(),
                        a.getDescription() != null && a.getDescription().length() > 100
                                ? a.getDescription().substring(0, 100) + "..."
                                : a.getDescription()))
                .collect(Collectors.toList());
    }

    private List<String> parseInterestTags(String interests) {
        if (!StringUtils.hasText(interests)) {
            return Collections.emptyList();
        }
        List<String> tags = new java.util.ArrayList<>();
        String lower = interests.toLowerCase();
        if (lower.contains("历史") || lower.contains("history") || lower.contains("文化") || lower.contains("culture"))
            tags.add("culture");
        if (lower.contains("自然") || lower.contains("nature") || lower.contains("风景") || lower.contains("scenic"))
            tags.add("nature");
        if (lower.contains("美食") || lower.contains("food") || lower.contains("吃"))
            tags.add("food");
        if (lower.contains("购物") || lower.contains("shopping"))
            tags.add("shopping");
        if (lower.contains("亲子") || lower.contains("family") || lower.contains("孩子"))
            tags.add("family");
        if (lower.contains("摄影") || lower.contains("photo"))
            tags.add("photography");
        if (lower.contains("户外") || lower.contains("outdoor") || lower.contains("冒险"))
            tags.add("activity");
        return tags.isEmpty() ? Collections.emptyList() : tags;
    }
}
