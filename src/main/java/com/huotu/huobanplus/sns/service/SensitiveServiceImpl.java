/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.huobanplus.sns.service;

import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by jin on 2016/10/18.
 */
@Service
public class SensitiveServiceImpl implements SensitiveService {
    @Override
    public boolean ContainSensitiveWords(String content) throws IOException {
        return false;
    }
}
