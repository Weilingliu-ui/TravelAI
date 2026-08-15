package com.travelai.travelai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travelai.travelai.entity.Favorite;
import com.travelai.travelai.entity.TravelPlan;

import java.util.List;

public interface FavoriteService extends IService<Favorite> {

    /** 收藏攻略 */
    boolean addFavorite(Long travelPlanId);

    /** 取消收藏 */
    boolean removeFavorite(Long travelPlanId);

    /** 是否已收藏 */
    boolean isFavorited(Long travelPlanId);

    /** 我的收藏列表 */
    List<TravelPlan> listMyFavorites();
}
