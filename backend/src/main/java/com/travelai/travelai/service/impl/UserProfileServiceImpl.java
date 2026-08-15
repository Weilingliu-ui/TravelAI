package com.travelai.travelai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelai.travelai.entity.UserProfile;
import com.travelai.travelai.mapper.UserProfileMapper;
import com.travelai.travelai.security.SecurityUtils;
import com.travelai.travelai.service.UserProfileService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {

    @Resource
    private UserProfileMapper userProfileMapper;

    @Resource
    private SecurityUtils securityUtils;

    @Override
    public UserProfile getOrCreateMyProfile() {
        Long userId = securityUtils.getCurrentUserId();
        UserProfile profile = getOne(new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, userId));
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
            save(profile);
        }
        return profile;
    }

    @Override
    public void updateHomeCity(String city) {
        UserProfile profile = getOrCreateMyProfile();
        profile.setHomeCity(city);
        updateById(profile);
    }
}
