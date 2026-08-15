package com.travelai.travelai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelai.travelai.entity.Favorite;
import com.travelai.travelai.entity.TravelPlan;
import com.travelai.travelai.mapper.FavoriteMapper;
import com.travelai.travelai.mapper.TravelPlanMapper;
import com.travelai.travelai.security.SecurityUtils;
import com.travelai.travelai.service.FavoriteService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private static final String ITEM_TYPE_PLAN = "plan";

    @Resource
    private FavoriteMapper favoriteMapper;

    @Resource
    private TravelPlanMapper travelPlanMapper;

    @Resource
    private SecurityUtils securityUtils;

    @Override
    public boolean addFavorite(Long travelPlanId) {
        Long userId = securityUtils.getCurrentUserId();

        // 幂等：已收藏直接返回 true
        Favorite existing = getOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getItemType, ITEM_TYPE_PLAN)
                .eq(Favorite::getItemId, travelPlanId));
        if (existing != null) {
            return true;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setItemType(ITEM_TYPE_PLAN);
        favorite.setItemId(travelPlanId);
        return save(favorite);
    }

    @Override
    public boolean removeFavorite(Long travelPlanId) {
        Long userId = securityUtils.getCurrentUserId();
        return remove(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getItemType, ITEM_TYPE_PLAN)
                .eq(Favorite::getItemId, travelPlanId));
    }

    @Override
    public boolean isFavorited(Long travelPlanId) {
        Long userId = securityUtils.getCurrentUserId();
        return count(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getItemType, ITEM_TYPE_PLAN)
                .eq(Favorite::getItemId, travelPlanId)) > 0;
    }

    @Override
    public List<TravelPlan> listMyFavorites() {
        Long userId = securityUtils.getCurrentUserId();

        List<Favorite> favorites = list(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getItemType, ITEM_TYPE_PLAN)
                .orderByDesc(Favorite::getCreatedAt));

        if (favorites.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> planIds = favorites.stream()
                .map(Favorite::getItemId)
                .collect(Collectors.toList());

        return travelPlanMapper.selectBatchIds(planIds)
                .stream()
                .filter(p -> p.getDeleted() == 0)
                .collect(Collectors.toList());
    }
}
