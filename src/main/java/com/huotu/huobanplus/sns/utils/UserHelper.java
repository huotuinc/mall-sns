/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.huobanplus.sns.utils;

import com.huotu.huobanplus.sns.boot.PublicParameterHolder;
import com.huotu.huobanplus.sns.entity.User;
import com.huotu.huobanplus.sns.exception.NeedLoginException;
import com.huotu.huobanplus.sns.model.AppPublicModel;
import com.huotu.huobanplus.sns.model.common.AppCode;

import java.util.Objects;

/**
 * Created by jin on 2016/10/10.
 */
public class UserHelper {

    public static User getUser() throws NeedLoginException {
        AppPublicModel model = PublicParameterHolder.getParameters();
        if (Objects.isNull(model) || Objects.isNull(model.getCurrentUser()))
            throw new NeedLoginException(AppCode.ERROR_USER_NEED_LOGIN.getValue(), AppCode.ERROR_USER_NEED_LOGIN.getName());
        return model.getCurrentUser();
    }
}
