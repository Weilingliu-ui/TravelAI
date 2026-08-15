package com.travelai.travelai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelai.travelai.common.exception.BusinessException;
import com.travelai.travelai.common.response.ResultCode;
import com.travelai.travelai.entity.Comment;
import com.travelai.travelai.entity.User;
import com.travelai.travelai.entity.UserProfile;
import com.travelai.travelai.mapper.CommentMapper;
import com.travelai.travelai.mapper.UserMapper;
import com.travelai.travelai.mapper.UserProfileMapper;
import com.travelai.travelai.security.SecurityUtils;
import com.travelai.travelai.service.CommentService;
import com.travelai.travelai.vo.CommentVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserProfileMapper userProfileMapper;

    @Resource
    private SecurityUtils securityUtils;

    @Override
    public CommentVO addComment(Long attractionId, String content, Integer rating) {
        Long userId = securityUtils.getCurrentUserId();

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setAttractionId(attractionId);
        comment.setContent(content);
        comment.setRating(rating != null ? rating : 5);
        save(comment);

        return toVO(comment, userId);
    }

    @Override
    public IPage<CommentVO> pageByAttraction(Long attractionId, int pageNum, int size) {
        IPage<Comment> page = page(new Page<>(pageNum, size),
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getAttractionId, attractionId)
                        .orderByDesc(Comment::getCreatedAt));

        List<Long> userIds = page.getRecords().stream()
                .map(Comment::getUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> nicknames = getNicknameMap(userIds);

        IPage<CommentVO> voPage = new Page<>(pageNum, size, page.getTotal());
        voPage.setRecords(page.getRecords().stream()
                .map(c -> {
                    CommentVO vo = new CommentVO();
                    vo.setId(c.getId());
                    vo.setUserId(c.getUserId());
                    vo.setNickname(nicknames.getOrDefault(c.getUserId(), "用户"));
                    vo.setAttractionId(c.getAttractionId());
                    vo.setContent(c.getContent());
                    vo.setRating(c.getRating());
                    vo.setCreatedAt(c.getCreatedAt());
                    return vo;
                })
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public boolean deleteComment(Long commentId) {
        Long userId = securityUtils.getCurrentUserId();
        Comment comment = getById(commentId);
        if (comment == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "评论不存在");
        }
        if (!userId.equals(comment.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只能删除自己的评论");
        }
        return removeById(commentId);
    }

    private CommentVO toVO(Comment comment, Long userId) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setUserId(userId);
        vo.setNickname(getNickname(userId));
        vo.setAttractionId(comment.getAttractionId());
        vo.setContent(comment.getContent());
        vo.setRating(comment.getRating());
        vo.setCreatedAt(comment.getCreatedAt());
        return vo;
    }

    private String getNickname(Long userId) {
        UserProfile profile = userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, userId));
        if (profile != null && profile.getNickname() != null) {
            return profile.getNickname();
        }
        User user = userMapper.selectById(userId);
        return user != null ? user.getUsername() : "用户";
    }

    private Map<Long, String> getNicknameMap(List<Long> userIds) {
        if (userIds.isEmpty()) return Map.of();
        List<UserProfile> profiles = userProfileMapper.selectList(
                new LambdaQueryWrapper<UserProfile>().in(UserProfile::getUserId, userIds));
        return profiles.stream()
                .collect(Collectors.toMap(UserProfile::getUserId, p -> p.getNickname() != null ? p.getNickname() : "用户"));
    }
}
