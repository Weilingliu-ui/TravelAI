package com.travelai.travelai.controller;

import com.travelai.travelai.common.response.Result;
import com.travelai.travelai.entity.UserProfile;
import com.travelai.travelai.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "用户画像", description = "个人信息/出发地设置")
@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    @Resource
    private UserProfileService userProfileService;

    @Operation(summary = "获取我的画像")
    @GetMapping
    public Result<UserProfile> getProfile() {
        return Result.success(userProfileService.getOrCreateMyProfile());
    }

    @Operation(summary = "更新出发城市")
    @PutMapping("/home-city")
    public Result<Void> updateHomeCity(
            @Parameter(description = "城市名称") @RequestBody Map<String, String> body) {
        userProfileService.updateHomeCity(body.get("homeCity"));
        return Result.success();
    }
}
