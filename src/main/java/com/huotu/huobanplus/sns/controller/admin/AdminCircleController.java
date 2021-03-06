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
import com.huotu.huobanplus.sns.entity.Circle;
import com.huotu.huobanplus.sns.model.admin.AdminArticlePageModel;
import com.huotu.huobanplus.sns.model.admin.CircleListModel;
import com.huotu.huobanplus.sns.model.admin.CircleSearchModel;
import com.huotu.huobanplus.sns.repository.CircleRepository;
import com.huotu.huobanplus.sns.service.ArticleService;
import com.huotu.huobanplus.sns.service.CircleService;
import com.huotu.huobanplus.sns.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * 圈子
 * Created by slt on 2016/10/11.
 */
@Controller
@RequestMapping("/top/circle")
public class AdminCircleController {
    @Autowired
    private CircleService circleService;

    @Autowired
    private CircleRepository circleRepository;

    @Autowired
    private ArticleService articleService;


    /**
     * 根据查询model返回圈子列表
     *
     * @param customerId        商户ID
     * @param circleSearchModel 查询model
     * @return 分页信息
     * @throws Exception
     */
    @RequestMapping(value = "/getCircleList", method = RequestMethod.POST)
    @ResponseBody
    public ModelMap getCircleList(@CustomerId Long customerId, @RequestBody CircleSearchModel circleSearchModel) throws Exception {
        circleSearchModel.setCustomerId(customerId);
        Page<Circle> circles = circleService.findCircleList(circleSearchModel);
        List<CircleListModel> models = circleService.findCircleListModel(circles.getContent());

        ModelMap modelMap = new ModelMap();
        modelMap.put("data", models);
        modelMap.put("total", circles.getTotalElements());
        modelMap.put("totalPage", circles.getTotalPages());
        return modelMap;

    }

    /**
     * 根据圈子id,打开编辑页面,如果id为空则是新建
     *
     * @param id    圈子ID
     * @param model 返回的model
     * @return 修改圈子的视图模板
     * @throws Exception
     */
    @RequestMapping(value = "/editCircle", method = RequestMethod.GET)
    public String editCircle(Long id, Model model) throws Exception {
        String view = "/admin/circle/modifyCircle";
        Circle circle = null;
        if (id != null) {
            circle = circleRepository.findOne(id);
        }
        if (circle == null) {
            circle = new Circle();
        }
        CircleListModel circleListModel = circleService.circleToDetailsCircleModel(circle);
        model.addAttribute("circleListModel", circleListModel);
        return view;
    }

    /**
     * 保存圈子
     *
     * @param circleListModel 圈子的model
     * @return 只要正常返回就说明保存成功
     * @throws Exception
     */
    @RequestMapping(value = "saveCircle", method = RequestMethod.POST)
    @ResponseBody
    public ModelMap saveCircle(@CustomerId Long customerId, @RequestBody CircleListModel circleListModel) throws Exception {
        ModelMap modelMap = new ModelMap();
        circleListModel.setCustomerId(customerId);
        if (circleListModel.getCircleId() == null) {
            circleService.addCircle(circleListModel);
        } else {
            circleService.updateCircle(circleListModel);
        }
        return modelMap;
    }

    /**
     * 推荐圈子首页
     *
     * @param model 返回的数据model
     * @return 视图模板
     * @throws IOException
     */
    @RequestMapping(value = "/commandIndex", method = RequestMethod.GET)
    public String commandIndex(Model model) throws IOException {
        List<Circle> list = circleService.findBySuggested(true);
        model.addAttribute("list", list);
//        List<Long> idList = new ArrayList<>();
//        list.stream().forEach(t -> idList.add(t.getId()));
//        model.addAttribute("idList", idList);
        return "/admin/circle/circleCommand";
    }

    @Transactional
    @RequestMapping(value = "/updateSuggest", method = RequestMethod.POST)
    @ResponseBody
    public ModelMap updateSuggest(Long[] ids, boolean suggested) throws IOException {
        for (Long id : ids) {
            circleRepository.updateSuggest(suggested, id);
        }
        return ResultUtil.success();
    }


    @RequestMapping("/articleList/{articleType}")
    public String articleList(@PathVariable("articleType") Integer articleType, Model model) {
        model.addAttribute("articleType", articleType);
        return "admin/circle/articleList";
    }

    @RequestMapping("/articleList.do")
    @ResponseBody
    public AdminArticlePageModel list(@CustomerId Long customerId, Integer articleType, String name, Integer pageNo, Integer pageSize) {
        return articleService.getAdminArticleList(customerId, articleType, name, pageNo, pageSize);
    }

    @RequestMapping("/articleEdit/{articleType}/{type}/{id}")
    public String articleEdit(@PathVariable("articleType") Integer articleType
            , @PathVariable("type") String type
            , @PathVariable("id") Long id, Model model) throws URISyntaxException {

        model.addAttribute("data", articleService.getAdminArticle(type, articleType, id));
        return "admin/circle/articleEdit";
    }

    @RequestMapping("/articleEdit.save")
    public String articleEditSave(@CustomerId Long customerId, Integer articleType, Long id
            , String name, Long userId, String pictureUrl, String content
            , String summary, Integer categoryId, Long circleId, String adConent, String tags) throws Exception {

        articleService.save(customerId, articleType, id, name, userId, pictureUrl, content, summary, categoryId, circleId, adConent, tags);
        return "redirect:/top/circle/articleList/" + articleType;
    }
}
