/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.huobanplus.sns.service.impl;

import com.huotu.huobanplus.sns.entity.Report;
import com.huotu.huobanplus.sns.entity.User;
import com.huotu.huobanplus.sns.exception.LogException;
import com.huotu.huobanplus.sns.model.admin.ReportListModel;
import com.huotu.huobanplus.sns.model.admin.ReportSearchModel;
import com.huotu.huobanplus.sns.model.common.ReportTargetType;
import com.huotu.huobanplus.sns.repository.ReportRepository;
import com.huotu.huobanplus.sns.service.ReportService;
import com.huotu.huobanplus.sns.utils.UserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jin on 2016/10/18.
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Override
    public void report(ReportTargetType type, Long id, String note) throws IOException, LogException {
        Report report = new Report();
        report.setContent(note);
        User user = UserHelper.getUser();
        report.setReportTargetType(type);
        report.setUser(user);
        report.setTargetId(id);
        report.setDate(new Date());
        reportRepository.save(report);
    }

    @Override
    public Page<Report> findReportList(ReportSearchModel searchModel) throws IOException {
        Sort sort;
        if (StringUtils.isEmpty(searchModel.getSortField())) {
            sort = new Sort(Sort.Direction.DESC, "date", "reportTargetType");
        } else {
            Sort.Direction sd = searchModel.getAscOrdesc() == 0 ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = new Sort(sd, searchModel.getSortField());
        }
        Specification<Report> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(searchModel.getName())) {
                predicates.add(criteriaBuilder.like(root.get("user").get("nickName").as(String.class), "%" + searchModel.getName() + "%"));
            }
            if (!StringUtils.isEmpty(searchModel.getReportTargetName())) {
                String typeName = searchModel.getReportTargetName();
                ReportTargetType reportTargetType=ReportTargetType.valueOf(typeName);
                predicates.add(criteriaBuilder.equal(root.get("reportTargetType").as(ReportTargetType.class), reportTargetType));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<Report> circleList = reportRepository.findAll(
                specification, new PageRequest(searchModel.getPageNo(), searchModel.getPageSize(), sort));
        return circleList;
    }

    @Override
    public List<ReportListModel> getReportListModelList(List<Report> reports) {
        List<ReportListModel> models=new ArrayList<>();
        if(reports==null){
            return models;
        }
        reports.forEach(report->{
            ReportListModel model=new ReportListModel();
            model.setReportId(report.getId());
            model.setReportName(report.getUser()==null?"":report.getUser().getNickName());
            model.setTargetId(report.getTargetId());
            model.setTypeName(report.getReportTargetType()==null?"":report.getReportTargetType().getName());
            model.setContent(report.getContent());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            model.setCreateDate(dateFormat.format(report.getDate()));
            models.add(model);
        });
        return models;
    }
}
