package com.huotu.huobanplus.sns.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/29.
 */
@MappedSuperclass
@Getter
@Setter
public  class AbstractArticle implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 商家
     */
    private Long customerId;

    /**
     * 发布用户
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    private User publisher;

    /**
     * 文章标题
     */
    @Column(length = 100)
    private String name;

    /**
     * 图片地址
     */
    @Column(length = 200)
    private String pictureUrl;

    /**
     * 文章内容
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String content;

    /**
     * 发布时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;


    /**
     * 点赞量
     */
    private Long click;

    /**
     * 浏览量
     */
    private Long view;

    /***
     * 回复评论量
     */
    private Long comments;

    /**
     * 是否置顶
     */
    private Boolean top;
}
