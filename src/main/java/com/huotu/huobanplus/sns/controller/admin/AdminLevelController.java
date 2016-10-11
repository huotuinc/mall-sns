/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.huobanplus.sns.controller.admin;

import com.huotu.huobanplus.sns.entity.Level;
import com.huotu.huobanplus.sns.repository.LevelRepository;
import com.huotu.huobanplus.sns.repository.UserRepository;
import com.huotu.huobanplus.sns.service.CommonConfigService;
import com.huotu.huobanplus.sns.utils.ContractHelper;
import com.huotu.huobanplus.sns.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by jin on 2016/10/11.
 */
@Controller
@RequestMapping(value = "/top/level")
public class AdminLevelController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private CommonConfigService commonConfigService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(@RequestParam(required = false) Integer page,
                        @RequestParam(required = false) Integer pageSize, Model model) throws IOException {
        if (Objects.isNull(page)) page = ContractHelper.list_page;
        if (Objects.isNull(pageSize)) pageSize = ContractHelper.list_pageSize;
        Sort sort = new Sort(Sort.Direction.ASC, "experience");
        Pageable pageable = new PageRequest(page - 1, pageSize, sort);
        Page<Level> pages = levelRepository.findAll(pageable);
        Long count = pages.getTotalElements();
        int pageCount = Integer.parseInt(count.toString()) / pageSize + 1;
        model.addAttribute("total", pages.getTotalElements());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("page", page);
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("list", pages.getContent());
        model.addAttribute("url", commonConfigService.getWebUrl() + "/top/level/index?page=");
        return "/admin/user/levelList";
    }

    @Transactional
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ModelMap save(
            @RequestParam(required = false) Long id, @RequestParam String name, @RequestParam Long experience
    ) throws IOException {
        Level level;
        if (Objects.isNull(id)) level = new Level();
        else level = levelRepository.findOne(id);
        level.setName(name);
        level.setExperience(experience);
        levelRepository.save(level);
        ModelMap map = new ModelMap();
        map.addAttribute("success", true);
        return map;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public ModelMap delete(@RequestParam Long id) throws IOException {
        Long count = userRepository.countByLevelId(id);
        if (count.intValue() > 0) {
            return ResultUtil.failure("该等级已有用户，无法删除");
        }
        levelRepository.delete(id);
        return ResultUtil.success();
    }

    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    @ResponseBody
    public ModelMap getOne(@RequestParam Long id) throws IOException {
        Level level = levelRepository.getOne(id);
        return ResultUtil.success(null, level);
    }
}