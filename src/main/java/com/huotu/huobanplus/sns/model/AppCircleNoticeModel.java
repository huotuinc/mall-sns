package com.huotu.huobanplus.sns.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Administrator on 2016/9/29.
 */
@Getter
@Setter
@EqualsAndHashCode
public class AppCircleNoticeModel {
    /**
     * 公告标题
     */
    private String name;
    /**
     * 公告地址
     */
    private String url;
}
