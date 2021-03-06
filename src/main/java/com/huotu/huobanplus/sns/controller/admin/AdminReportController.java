/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.huobanplus.sns.controller.admin;

import com.huotu.huobanplus.sns.annotation.CustomerId;
import com.huotu.huobanplus.sns.entity.Report;
import com.huotu.huobanplus.sns.entity.User;
import com.huotu.huobanplus.sns.model.admin.ReportDetailsModel;
import com.huotu.huobanplus.sns.model.admin.ReportListModel;
import com.huotu.huobanplus.sns.model.admin.ReportSearchModel;
import com.huotu.huobanplus.sns.repository.ReportRepository;
import com.huotu.huobanplus.sns.repository.UserRepository;
import com.huotu.huobanplus.sns.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 举报
 * Created by slt on 2016/10/11.
 */
@Controller
@RequestMapping("/top/report")
public class AdminReportController {
    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;


    /**
     * 返回举报列表
     * @param reportSearchModel 举报查询model
     * @param customerId        商户ID
     * @return  查询结果
     * @throws Exception
     */
    @RequestMapping(value = "/getReportList", method = RequestMethod.POST)
    @ResponseBody
    public ModelMap getReportList(@CustomerId Long customerId, @RequestBody ReportSearchModel reportSearchModel) throws Exception {

        reportSearchModel.setCustomerId(customerId);
        Page<Report> reports = reportService.findReportList(reportSearchModel);

        List<ReportListModel> models=reportService.getReportListModelList(reports.getContent());
        ModelMap modelMap = new ModelMap();
        modelMap.put("data",models);
        modelMap.put("total", reports.getTotalElements());
        modelMap.put("totalPage", reports.getTotalPages());
        return modelMap;

    }

    /**
     * 查看举报具体信息
     *
     * @param id    举报ID
     * @param model 具体举报信息model
     * @return      返回具体举报信息
     * @throws Exception
     */
    @RequestMapping(value = "/editReport",method = RequestMethod.GET)
    public String editCircle(@RequestParam(required = true) Long id, Model model) throws Exception{
        String view="/admin/report/report_details";
        Report report= reportRepository.findOne(id);

        ReportDetailsModel reportDetailsModel=reportService.getReportDetails(report);

        model.addAttribute("model",reportDetailsModel);
        return view;
    }

    /**
     * 禁止用户操作，type,0：操作发帖，1：操作发言
     * status,0:禁止，1：允许
     * @param userId
     * @param type
     * @param status
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/forbidOperation")
    @ResponseBody
    public ModelMap forbidOperation(@RequestParam(required = true) Long userId,
                                    @RequestParam(required = true) Integer type,
                                    @RequestParam(required = true) String status) throws Exception{
        ModelMap modelMap=new ModelMap();

        User user=userRepository.findOne(userId);
        if(user==null){
            modelMap.addAttribute("status",500);
            modelMap.addAttribute("message","无法找到用户");
            return modelMap;
        }
        if(type!=1&&type!=0){
            modelMap.addAttribute("status",500);
            modelMap.addAttribute("message","修改类型错误");
            return modelMap;

        }

        if(!"1".equals(status)&&!"0".equals(status)){
            modelMap.addAttribute("status",500);
            modelMap.addAttribute("message","修改状态错误");
            return modelMap;
        }
        StringBuilder power=new StringBuilder(reportService.formatUserPower(user.getPower()));
        power.replace(type,type+1,status);
        user.setPower(power.toString());
        userRepository.save(user);
        modelMap.addAttribute("status",200);
        modelMap.addAttribute("message","修改成功");
        return modelMap;
    }
}
