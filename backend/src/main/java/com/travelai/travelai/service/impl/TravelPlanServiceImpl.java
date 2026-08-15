package com.travelai.travelai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelai.travelai.common.exception.BusinessException;
import com.travelai.travelai.common.response.ResultCode;
import com.travelai.travelai.entity.TravelPlan;
import com.travelai.travelai.mapper.TravelPlanMapper;
import com.travelai.travelai.security.SecurityUtils;
import com.travelai.travelai.service.TravelPlanService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 旅行计划服务实现
 * <p>
 * 所有操作强制与当前登录用户绑定，禁止跨用户访问。
 *
 * @author TravelAI Team
 */
@Service
public class TravelPlanServiceImpl extends ServiceImpl<TravelPlanMapper, TravelPlan> implements TravelPlanService {

    @Resource
    private TravelPlanMapper travelPlanMapper;

    @Resource
    private SecurityUtils securityUtils;

    @Override
    public boolean save(TravelPlan entity) {
        // 自动绑定当前登录用户
        entity.setUserId(securityUtils.getCurrentUserId());
        return super.save(entity);
    }

    @Override
    public List<TravelPlan> listByUser() {
        // 只查询当前用户的攻略
        Long userId = securityUtils.getCurrentUserId();
        return list(new LambdaQueryWrapper<TravelPlan>()
                .eq(TravelPlan::getUserId, userId)
                .orderByDesc(TravelPlan::getCreatedAt));
    }

    @Override
    public boolean updateById(TravelPlan entity) {
        // 校验数据归属
        checkOwnership(entity.getId());
        // 防止修改归属
        entity.setUserId(null);
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(java.io.Serializable id) {
        // 校验数据归属
        checkOwnership((Long) id);
        return super.removeById(id);
    }

    @Override
    public TravelPlan getById(java.io.Serializable id) {
        TravelPlan plan = super.getById(id);
        if (plan == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "攻略不存在");
        }
        // 校验数据归属
        Long currentUserId = securityUtils.getCurrentUserId();
        if (!currentUserId.equals(plan.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权访问该攻略");
        }
        return plan;
    }

    /**
     * 校验攻略是否属于当前用户
     */
    private void checkOwnership(Long planId) {
        if (planId == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "攻略ID不能为空");
        }
        TravelPlan plan = super.getById(planId);
        if (plan == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "攻略不存在");
        }
        Long currentUserId = securityUtils.getCurrentUserId();
        if (!currentUserId.equals(plan.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权操作该攻略");
        }
    }
}
