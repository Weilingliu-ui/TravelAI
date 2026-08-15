package com.travelai.travelai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travelai.travelai.entity.UserProfile;

public interface UserProfileService extends IService<UserProfile> {

    /** 获取或创建当前用户画像 */
    UserProfile getOrCreateMyProfile();

    /** 更新当前用户的常用出发城市 */
    void updateHomeCity(String city);
}
