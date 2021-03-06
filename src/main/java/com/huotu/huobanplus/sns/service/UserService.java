/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.huobanplus.sns.service;

import com.huotu.huobanplus.sns.entity.Article;
import com.huotu.huobanplus.sns.entity.Level;
import com.huotu.huobanplus.sns.entity.User;
import com.huotu.huobanplus.sns.entity.UserArticle;
import com.huotu.huobanplus.sns.exception.*;
import com.huotu.huobanplus.sns.model.AppCircleArticleModel;
import com.huotu.huobanplus.sns.model.AppUserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 用户服务
 * Created by Administrator on 2016/10/9.
 */
public interface UserService {


    /**
     * 获取用户Id
     * 从加密的cookie中取出数据
     *
     * @param request
     * @return
     */
//    Long getMerchantUserId(HttpServletRequest request) throws Exception;
//
//    /**
//     * 对用户的id进行加密
//     * 放入cookie中
//     */
//    void setUserId(Long userId, HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 根据条件查询用户列表
     *
     * @param nickName         用户昵称
     * @param authenticationId 身份id
     * @param levelId          等级id
     * @param customerId       商户id
     * @param pageable         分页
     * @return 用户列表
     * @throws IOException
     */
    Page<User> findByNickNameAndAuthenticationIdAndLevelIdAndCustomerId(String nickName,
                                                                        Integer authenticationId,
                                                                        Long levelId, Long customerId, Pageable pageable) throws IOException;

    /**
     * 浏览App用户
     *
     * @param userId
     * @return
     */
    AppUserModel getAppUser(Long userId);

    AppCircleArticleModel[] changeModelArray(List<UserArticle> articles) throws IOException;

    AppCircleArticleModel[] changeModelArrayByArticle(List<Article> articles) throws IOException;

    /**
     * 用户登录
     *
     * @param customerId
     * @param phone
     * @param password
     * @param openId
     * @param nickName
     * @param imageUrl
     * @return
     * @throws UnsupportedEncodingException
     * @throws UserNamePasswordInvoidException
     * @throws MobileInvoidException
     * @throws PasswordLengthLackException
     */
    String userLogin(Long customerId, String phone, String password
            , String openId
            , String nickName
            , String imageUrl) throws UnsupportedEncodingException, UserNamePasswordInvoidException, MobileInvoidException, PasswordLengthLackException, MobileNotExistException;


    String userRegister(Long customerId, String phone, String code
            , String password
            , String openId
            , String nickName
            , String imageUrl) throws VerificationCodeInvoidException, VerificationCodeDuedException, UnsupportedEncodingException, MobileInvoidException, PasswordLengthLackException, MobileExistException;

    /**
     * 注册一个用户
     *
     * @param customerId
     * @param userId
     * @param mobile
     * @param openId
     * @param nickName
     * @param imageUrl
     * @return
     */
    User register(Long customerId
            , Long userId
            , String mobile
            , String openId
            , String nickName
            , String imageUrl);

    /**
     * 微信登录
     *
     * @param customerId
     * @param openId
     * @param nickName
     * @param imageUrl
     * @return
     */
    String weixinLogin(Long customerId
            , String openId
            , String nickName
            , String imageUrl) throws WeixinLoginFailException;


    /**
     * 创建缺省的用户等级
     *
     * @param customerId
     * @return
     */
    Level createDefaultLevel(Long customerId);
//    /**
//     * 注册用户
//     *
//     * @param customerId 商家Id
//     * @param mobile     手机号
//     * @param openId     微信openid
//     * @param nickName   用户昵称
//     * @param imageUrl   用户头像
//     */
//    User register(Long customerId
//            , String mobile
//            , String openId
//            , String nickName
//            , String imageUrl);
}
