/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.huobanplus.sns.service.impl;

import com.huotu.huobanplus.sns.boot.PublicParameterHolder;
import com.huotu.huobanplus.sns.entity.Circle;
import com.huotu.huobanplus.sns.entity.User;
import com.huotu.huobanplus.sns.entity.UserCircle;
import com.huotu.huobanplus.sns.exception.ConcernException;
import com.huotu.huobanplus.sns.exception.LogException;
import com.huotu.huobanplus.sns.model.AppPublicModel;
import com.huotu.huobanplus.sns.repository.CircleRepository;
import com.huotu.huobanplus.sns.repository.UserCircleRepository;
import com.huotu.huobanplus.sns.service.UserCircleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by jin on 2016/10/10.
 */
@Service
public class UserCircleServiceImpl implements UserCircleService {

    private final static String circleFlag = "_circle_";
    @Autowired
    private CircleRepository circleRepository;
    @Autowired
    private UserCircleRepository userCircleRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private User getUser() throws LogException {
        AppPublicModel model = PublicParameterHolder.getParameters();
        if (Objects.isNull(model) || Objects.isNull(model.getCurrentUser()))
            throw new LogException("您尚未登录");
        return model.getCurrentUser();
    }

    @Transactional
    @Override
    public synchronized void concern(Long id) throws ConcernException, LogException, IOException {
        User user = getUser();
        Circle circle = circleRepository.getOne(id);
        List<UserCircle> userCircles = userCircleRepository.findByUserAndCircle(user, circle);
        if (Objects.nonNull(userCircles) && userCircles.size() > 0)
            throw new ConcernException("您已关注该圈子");
        UserCircle userCircle = new UserCircle();
        userCircle.setCircle(circle);
        userCircle.setDate(new Date());
        userCircle.setUser(user);
        userCircleRepository.save(userCircle);
        BoundHashOperations<String, String, Long> circleOperations = redisTemplate
                .boundHashOps(circleFlag + id);
        Long userAmount = circleOperations.get("userAmount");
        if (null == userAmount) {
            circleOperations.put("userAmount", 1L);
        } else {
            circleOperations.put("userAmount", userAmount + 1L);
        }
    }

    @Override
    public synchronized void cancelConcern(Long id) throws ConcernException, LogException, IOException {
        User user = getUser();
        Circle circle = circleRepository.getOne(id);
        List<UserCircle> userCircles = userCircleRepository.findByUserAndCircle(user, circle);
        if (Objects.isNull(userCircles) || userCircles.size() == 0)
            throw new ConcernException("您已经取消关注该圈子~");
        for (UserCircle userCircle : userCircles) {
            userCircleRepository.delete(userCircle);
        }
        BoundHashOperations<String, String, Long> circleOperations = redisTemplate
                .boundHashOps(circleFlag + id);
        Long userAmount = circleOperations.get("userAmount");
        circleOperations.put("userAmount", userAmount - userCircles.size());
    }
}
